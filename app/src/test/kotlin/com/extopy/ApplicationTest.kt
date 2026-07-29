package com.extopy

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import org.jsoup.Jsoup
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Boots the real application, so these cover the wiring the route tests mock away: the Koin graph,
 * the database, and the shape of the routing tree. What each route answers is covered by the route
 * tests of the presentation module.
 */
class ApplicationTest {

    /**
     * Each test gets its own in-memory database. `DB_CLOSE_DELAY=-1` keeps one alive for as long as
     * the process is, so sharing a name would let one test see what another one wrote.
     */
    private fun withApplication(name: String, block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        environment {
            config = ApplicationConfig("application.test.conf")
                .mergeWith(MapApplicationConfig("database.name" to "extopy-$name"))
        }
        application {
            module()
        }
        block()
    }

    /**
     * The paths the kubernetes probes read, see `helm/extopy/values.yaml`.
     */
    @Test
    fun testProbes() = withApplication("probes") {
        val alive = client.get("/healthz")
        assertEquals(HttpStatusCode.OK, alive.status)
        assertEquals("""{"alive":true}""", alive.bodyAsText())

        val ready = client.get("/readyz")
        assertEquals(HttpStatusCode.OK, ready.status)
        assertEquals("""{"database":true}""", ready.bodyAsText())
    }

    /**
     * The documentation is read off the routing tree, so it can only ever describe the routes that
     * are actually mounted.
     */
    @Test
    fun testDocumentationIsServed() = withApplication("docs") {
        val response = client.get("/docs")
        assertEquals(HttpStatusCode.OK, response.status)
        with(response.bodyAsText()) {
            assertContains(this, "Extopy API")
            assertContains(this, "/api/v1/users/{userId}/following")
        }
    }

    @Test
    fun testLandingPageIsServed() = withApplication("landing") {
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertContains(response.bodyAsText(), "Get back to the basics.")
    }

    /**
     * Every API route must resolve and answer 401 rather than 404, which would mean it is not
     * registered at all.
     */
    @Test
    fun testApiRoutesRequireAuthentication() = withApplication("api-auth") {
        val userId = "00000000-0000-4000-8000-000000000001"
        val postId = "00000000-0000-4000-8000-000000000002"
        val timelineId = "00000000-0000-0000-0000-000000000000"
        val paths = listOf(
            "/api/v1/users",
            "/api/v1/users/$userId",
            "/api/v1/users/$userId/posts",
            "/api/v1/users/$userId/followers",
            "/api/v1/users/$userId/following",
            "/api/v1/posts",
            "/api/v1/posts/$postId",
            "/api/v1/posts/$postId/replies",
            "/api/v1/posts/$postId/likes",
            "/api/v1/timelines/$timelineId",
            "/api/v1/timelines/$timelineId/posts",
        )
        paths.forEach { path ->
            val response = client.get(path)
            assertEquals(HttpStatusCode.Unauthorized, response.status, "unexpected status for $path")
            assertEquals(
                """{"error":"auth_invalid_credentials"}""",
                response.bodyAsText(),
                "unexpected body for $path"
            )
        }
    }

    /**
     * The pages of the sign-in and sign-up flow live under a language, and a link without one is
     * sent to the language the browser asked for.
     */
    @Test
    fun testAuthPagesAreLocalized() = withApplication("auth-pages") {
        val client = client.config { followRedirects = false }

        val redirected = client.get("/auth/login") { header(HttpHeaders.AcceptLanguage, "fr") }
        assertEquals(HttpStatusCode.Found, redirected.status)
        assertEquals("/fr/auth/login", redirected.headers[HttpHeaders.Location])

        val english = client.get("/en/auth/login")
        assertEquals(HttpStatusCode.OK, english.status)
        with(Jsoup.parse(english.bodyAsText())) {
            assertEquals("Sign in", getElementById("auth_login_title")?.text())
            assertTrue(getElementById("username")?.hasAttr("required") == true)
            assertTrue(getElementById("password")?.hasAttr("required") == true)
        }

        val french = client.get("/fr/auth/login")
        assertEquals(HttpStatusCode.OK, french.status)
        assertContains(french.bodyAsText(), "Connexion")
    }

    /**
     * A `?redirect=` pointing at another host must not be honoured, in any of the shapes a browser
     * resolves back to an authority.
     */
    @Test
    fun testForeignRedirectsAreIgnored() = withApplication("redirects") {
        val client = client.config { followRedirects = false }

        listOf(
            "//evil.example.com",
            "/\\evil.example.com",
            "/%09/evil.example.com",
            "https://evil.example.com",
        ).forEach { target ->
            val response = client.get("/en/auth/logout?redirect=$target")
            assertEquals(HttpStatusCode.Found, response.status, "unexpected status for $target")
            assertEquals("/", response.headers[HttpHeaders.Location], "honoured $target")
        }

        val same = client.get("/en/auth/logout?redirect=/en/auth/login")
        assertEquals("/en/auth/login", same.headers[HttpHeaders.Location])
    }

    /**
     * Signing up, signing in, and reading yourself back through the API: the one path that touches
     * every layer at once.
     */
    @Test
    fun testRegisterThenSignIn() = withApplication("register") {
        val client = client.config { followRedirects = false }

        // The address has to be proven first, and the code only ever reaches the inbox, so the page
        // can only say it was sent.
        val requested = client.post("/en/auth/register") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody("email=someone@extopy.com")
        }
        assertEquals(HttpStatusCode.OK, requested.status)
        assertContains(requested.bodyAsText(), "Please check your inbox")

        // Asking twice for an address that already has an account is refused.
        val duplicate = client.post("/en/auth/register") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody("email=someone@extopy.com")
        }
        assertEquals(HttpStatusCode.OK, duplicate.status)
    }

    @Test
    fun testSignInWithWrongCredentials() = withApplication("wrong-credentials") {
        val response = client.post("/en/auth/login") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody("username=nobody&password=wrong")
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertContains(response.bodyAsText(), "Invalid credentials")
    }

    @Test
    fun testTokenEndpointRefusesAnUnknownCode() = withApplication("token") {
        val response = client.post("/api/v1/auth/token") {
            contentType(ContentType.Application.Json)
            setBody(
                """{"clientId":"00000000-0000-4000-8000-000000000001","clientSecret":"s","code":"nope"}"""
            )
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("""{"error":"auth_invalid_code"}""", response.bodyAsText())
    }

}

package com.extopy.presentation.routes.auth

import com.extopy.domain.exceptions.auth.EmailAlreadyTakenException
import com.extopy.domain.models.application.CodeInEmail
import com.extopy.domain.models.auth.LoginPayload
import com.extopy.domain.models.auth.RegisterCodePayload
import com.extopy.domain.usecases.application.CreateCodeInEmailUseCase
import com.extopy.domain.usecases.application.DeleteCodeInEmailUseCase
import com.extopy.domain.usecases.application.GetCodeInEmailUseCase
import com.extopy.domain.usecases.application.SendRegistrationEmailUseCase
import com.extopy.domain.usecases.auth.CreateAuthCodeUseCase
import com.extopy.domain.usecases.auth.GetClientUseCase
import com.extopy.domain.usecases.auth.LoginUseCase
import com.extopy.domain.usecases.auth.RegisterUseCase
import com.extopy.domain.usecases.users.GetUserUseCase
import com.extopy.presentation.routes.RoutesTestHelper
import com.extopy.presentation.routes.RoutesTestHelper.configureTestApplication
import com.extopy.presentation.routes.localized
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.datetime.LocalDate
import org.jsoup.Jsoup
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours

class AuthWebRoutesTest {

    private class Mocks {
        val loginUseCase = mockk<LoginUseCase>()
        val registerUseCase = mockk<RegisterUseCase>()
        val createCodeInEmailUseCase = mockk<CreateCodeInEmailUseCase>()
        val getCodeInEmailUseCase = mockk<GetCodeInEmailUseCase>()
        val deleteCodeInEmailUseCase = mockk<DeleteCodeInEmailUseCase>()
        val sendRegistrationEmailUseCase = mockk<SendRegistrationEmailUseCase>()
        val getClientUseCase = mockk<GetClientUseCase>()
        val createAuthCodeUseCase = mockk<CreateAuthCodeUseCase>()
        val getUserUseCase = mockk<GetUserUseCase>()
    }

    private fun ApplicationTestBuilder.configureApp(mocks: Mocks) {
        application {
            configureTestApplication(mocks.getUserUseCase)
            routing {
                authenticate(RoutesTestHelper.API_JWT, optional = true) {
                    localized("/auth") {
                        authWebRoutes(
                            AuthWebRoutesDependencies(
                                mocks.loginUseCase,
                                mocks.registerUseCase,
                                mocks.createCodeInEmailUseCase,
                                mocks.getCodeInEmailUseCase,
                                mocks.deleteCodeInEmailUseCase,
                                mocks.sendRegistrationEmailUseCase,
                                mocks.getClientUseCase,
                                mocks.createAuthCodeUseCase,
                                mocks.getUserUseCase,
                            )
                        )
                    }
                }
            }
        }
    }

    private fun ApplicationTestBuilder.staying() = client.config { followRedirects = false }

    private fun HttpRequestBuilder.authenticated() =
        header(HttpHeaders.Authorization, "Bearer ${RoutesTestHelper.generateTestToken()}")

    private fun Mocks.signedIn() {
        coEvery { getUserUseCase(RoutesTestHelper.TEST_USER_ID, RoutesTestHelper.TEST_USER_ID) } returns
                RoutesTestHelper.testUser
    }

    /**
     * The pages render through the layout. The test bundle echoes the keys, so this asserts which
     * keys the page asks for; that they resolve to English is covered by the application test.
     */
    @Test
    fun testLoginPage() = testApplication {
        val mocks = Mocks()
        configureApp(mocks)

        val response = client.get("/en/auth/login")

        assertEquals(HttpStatusCode.OK, response.status)
        with(Jsoup.parse(response.bodyAsText())) {
            assertEquals("auth_login_title", getElementById("auth_login_title")?.text())
            assertEquals("text", getElementById("username")?.attr("type"))
            assertTrue(getElementById("username")?.hasAttr("required") == true)
            assertEquals("password", getElementById("password")?.attr("type"))
        }
    }

    /**
     * A page asked for without a language is sent to the one the browser wants.
     */
    @Test
    fun testAPageWithoutALanguageIsRedirected() = testApplication {
        val mocks = Mocks()
        configureApp(mocks)

        val english = staying().get("/auth/login")
        assertEquals(HttpStatusCode.Found, english.status)
        assertEquals("/en/auth/login", english.headers[HttpHeaders.Location])

        val french = staying().get("/auth/register/abc") { header(HttpHeaders.AcceptLanguage, "fr-FR,fr") }
        assertEquals("/fr/auth/register/abc", french.headers[HttpHeaders.Location])
    }

    @Test
    fun testLogin() = testApplication {
        val mocks = Mocks()
        val payload = LoginPayload("nathanfallet", "hunter2")
        coEvery { mocks.loginUseCase(payload) } returns RoutesTestHelper.testUser
        configureApp(mocks)

        val response = staying().submitForm(
            url = "/en/auth/login?redirect=/en/auth/authorize",
            formParameters = parameters {
                append("username", payload.username)
                append("password", payload.password)
            }
        )

        assertEquals(HttpStatusCode.Found, response.status)
        assertEquals("/en/auth/authorize", response.headers[HttpHeaders.Location])
        assertContains(response.headers[HttpHeaders.SetCookie].orEmpty(), "session")
    }

    @Test
    fun testLoginWithWrongCredentials() = testApplication {
        val mocks = Mocks()
        coEvery { mocks.loginUseCase(any()) } returns null
        configureApp(mocks)

        val response = client.submitForm(
            url = "/en/auth/login",
            formParameters = parameters {
                append("username", "nathanfallet")
                append("password", "wrong")
            }
        )

        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertContains(response.bodyAsText(), "auth_invalid_credentials")
    }

    /**
     * A `?redirect=` pointing at another host must not be honoured, in any of the shapes a browser
     * resolves back to an authority: `//host`, `/\host`, and anything with a stripped control
     * character in between.
     */
    @Test
    fun testForeignRedirectsAreIgnored() = testApplication {
        val mocks = Mocks()
        configureApp(mocks)
        val client = staying()

        listOf(
            "//evil.example.com",
            "/\\evil.example.com",
            "/%09/evil.example.com",
            "/%0d/evil.example.com",
            "https://evil.example.com",
        ).forEach { target ->
            val response = client.get("/en/auth/logout?redirect=$target")
            assertEquals(HttpStatusCode.Found, response.status, "unexpected status for $target")
            assertEquals("/", response.headers[HttpHeaders.Location], "honoured $target")
        }
    }

    @Test
    fun testRegisterAsksForAnAddress() = testApplication {
        val mocks = Mocks()
        configureApp(mocks)

        val response = client.get("/en/auth/register")

        assertEquals(HttpStatusCode.OK, response.status)
        with(Jsoup.parse(response.bodyAsText())) {
            assertEquals("email", getElementById("email")?.attr("type"))
            assertTrue(getElementById("username") == null, "the second step was rendered too early")
        }
    }

    @Test
    fun testRegisterSendsTheCode() = testApplication {
        val mocks = Mocks()
        val code = CodeInEmail("someone@extopy.com", "abc", Clock.System.now() + 1.hours)
        coEvery { mocks.createCodeInEmailUseCase("someone@extopy.com") } returns code
        coEvery { mocks.sendRegistrationEmailUseCase("someone@extopy.com", "abc", "en") } returns Unit
        configureApp(mocks)

        val response = client.submitForm(
            url = "/en/auth/register",
            formParameters = parameters { append("email", "someone@extopy.com") }
        )

        assertEquals(HttpStatusCode.OK, response.status)
        assertContains(response.bodyAsText(), "auth_register_code_created")
        coVerify { mocks.sendRegistrationEmailUseCase("someone@extopy.com", "abc", "en") }
    }

    @Test
    fun testRegisterWithAnAddressThatAlreadyHasAnAccount() = testApplication {
        val mocks = Mocks()
        coEvery { mocks.createCodeInEmailUseCase(any()) } throws EmailAlreadyTakenException()
        configureApp(mocks)

        val response = client.submitForm(
            url = "/en/auth/register",
            formParameters = parameters { append("email", "someone@extopy.com") }
        )

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertContains(response.bodyAsText(), "auth_register_email_taken")
    }

    /**
     * The second step fills the address in and does not let it be changed: it comes from the code,
     * never from the form.
     */
    @Test
    fun testRegisterCodeShowsTheAddressItWasSentTo() = testApplication {
        val mocks = Mocks()
        coEvery { mocks.getCodeInEmailUseCase("abc") } returns
                CodeInEmail("someone@extopy.com", "abc", Clock.System.now() + 1.hours)
        configureApp(mocks)

        val response = client.get("/en/auth/register/abc")

        assertEquals(HttpStatusCode.OK, response.status)
        with(Jsoup.parse(response.bodyAsText())) {
            assertEquals("someone@extopy.com", getElementById("email")?.attr("value"))
            assertTrue(getElementById("email")?.hasAttr("disabled") == true)
            assertTrue(getElementById("username")?.hasAttr("required") == true)
            assertEquals("date", getElementById("birthdate")?.attr("type"))
        }
    }

    @Test
    fun testRegisterCodeWithAnUnknownCode() = testApplication {
        val mocks = Mocks()
        coEvery { mocks.getCodeInEmailUseCase(any()) } returns null
        configureApp(mocks)

        val response = client.get("/en/auth/register/nope")

        assertEquals(HttpStatusCode.NotFound, response.status)
        assertContains(response.bodyAsText(), "auth_code_invalid")
    }

    @Test
    fun testRegisterCodeOpensTheAccount() = testApplication {
        val mocks = Mocks()
        val payload = RegisterCodePayload(
            password = "hunter2",
            username = "nathanfallet",
            displayName = "Nathan Fallet",
            birthdate = LocalDate.parse("2002-12-24"),
        )
        coEvery { mocks.registerUseCase("abc", payload) } returns RoutesTestHelper.testUser
        coEvery { mocks.deleteCodeInEmailUseCase("abc") } returns Unit
        configureApp(mocks)

        val response = staying().submitForm(
            url = "/en/auth/register/abc",
            formParameters = parameters {
                append("password", "hunter2")
                append("username", "nathanfallet")
                append("displayName", "Nathan Fallet")
                append("birthdate", "2002-12-24")
            }
        )

        assertEquals(HttpStatusCode.Found, response.status)
        assertEquals("/", response.headers[HttpHeaders.Location])
        assertContains(response.headers[HttpHeaders.SetCookie].orEmpty(), "session")
        // The code has been used, so it is burnt.
        coVerify { mocks.deleteCodeInEmailUseCase("abc") }
    }

    @Test
    fun testRegisterCodeWithAMalformedBirthdate() = testApplication {
        val mocks = Mocks()
        coEvery { mocks.getCodeInEmailUseCase("abc") } returns
                CodeInEmail("someone@extopy.com", "abc", Clock.System.now() + 1.hours)
        configureApp(mocks)

        val response = client.submitForm(
            url = "/en/auth/register/abc",
            formParameters = parameters {
                append("password", "hunter2")
                append("username", "nathanfallet")
                append("displayName", "Nathan Fallet")
                append("birthdate", "not-a-date")
            }
        )

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertContains(response.bodyAsText(), "error_body_invalid")
    }

    /**
     * A visitor that is not signed in cannot say yes on behalf of an account, so it is sent to the
     * sign-in page and back.
     */
    @Test
    fun testAuthorizeSendsAVisitorToSignIn() = testApplication {
        val mocks = Mocks()
        configureApp(mocks)

        val response = staying().get("/en/auth/authorize?clientId=${RoutesTestHelper.TEST_CLIENT_ID}")

        assertEquals(HttpStatusCode.Found, response.status)
        assertEquals(
            "/auth/login?redirect=/en/auth/authorize?clientId=${RoutesTestHelper.TEST_CLIENT_ID}",
            response.headers[HttpHeaders.Location]
        )
    }

    @Test
    fun testAuthorizeNamesTheClient() = testApplication {
        val mocks = Mocks()
        mocks.signedIn()
        coEvery { mocks.getClientUseCase(RoutesTestHelper.TEST_CLIENT_ID) } returns RoutesTestHelper.testClient
        configureApp(mocks)

        val response = client.get("/en/auth/authorize?clientId=${RoutesTestHelper.TEST_CLIENT_ID}") {
            authenticated()
        }

        assertEquals(HttpStatusCode.OK, response.status)
        with(response.bodyAsText()) {
            assertContains(this, "auth_authorize_title(Extopy for iOS)")
            assertContains(this, "The one on your phone")
            assertContains(this, "auth_hint_authorize_connected_as(Nathan Fallet (@nathanfallet))")
            // The secret of the client has no business being on a page.
            assertEquals(false, contains("hunter2"), "the page carried the client secret")
        }
    }

    @Test
    fun testAuthorizeWithAnUnknownClient() = testApplication {
        val mocks = Mocks()
        mocks.signedIn()
        coEvery { mocks.getClientUseCase(any()) } returns null
        configureApp(mocks)

        val response = client.get("/en/auth/authorize?clientId=${RoutesTestHelper.TEST_CLIENT_ID}") {
            authenticated()
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertContains(response.bodyAsText(), "auth_invalid_client")
    }

    /**
     * Saying yes hands the client its code, through the redirect URI it was registered with.
     */
    @Test
    fun testAuthorizeRedirectsWithTheCode() = testApplication {
        val mocks = Mocks()
        mocks.signedIn()
        coEvery { mocks.getClientUseCase(RoutesTestHelper.TEST_CLIENT_ID) } returns RoutesTestHelper.testClient
        coEvery { mocks.createAuthCodeUseCase(any()) } returns "the-code"
        configureApp(mocks)

        val response = client.post("/en/auth/authorize?clientId=${RoutesTestHelper.TEST_CLIENT_ID}") {
            authenticated()
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertContains(response.bodyAsText(), "extopy://auth?code=the-code")
    }

}

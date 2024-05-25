package me.nathanfallet.extopy.controllers.auth

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import me.nathanfallet.extopy.models.application.ExtopyJson
import me.nathanfallet.extopy.models.auth.RegisterPayload
import me.nathanfallet.extopy.plugins.*
import me.nathanfallet.ktorx.usecases.localization.IGetLocaleForCallUseCase
import org.jsoup.Jsoup
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthRouterTest {

    private fun installApp(application: ApplicationTestBuilder): HttpClient {
        application.environment {
            config = ApplicationConfig("application.test.conf")
        }
        application.application {
            configureI18n()
            configureKoin()
            configureSerialization()
            configureSecurity()
            configureTemplating()
        }
        return application.createClient {
            followRedirects = false
            install(ContentNegotiation) {
                json(ExtopyJson.json)
            }
        }
    }

    @Test
    fun testGetLoginRoute() = testApplication {
        val client = installApp(this)
        val controller = mockk<IAuthController>()
        val getLocaleForCallUseCase = mockk<IGetLocaleForCallUseCase>()
        val router = AuthRouter(controller, getLocaleForCallUseCase)
        every { controller.login() } returns Unit
        every { getLocaleForCallUseCase(any()) } returns Locale.ENGLISH
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/en/auth/login")
        assertEquals(HttpStatusCode.OK, response.status)
        val document = Jsoup.parse(response.bodyAsText())
        assertEquals(true, document.getElementById("auth_login_title")?.`is`("h1"))
        assertEquals(true, document.getElementById("username")?.`is`("input"))
        assertEquals("username", document.getElementById("username")?.attr("name"))
        assertEquals("text", document.getElementById("username")?.attr("type"))
        assertEquals(true, document.getElementById("username")?.hasAttr("required"))
        assertEquals(true, document.getElementById("password")?.`is`("input"))
        assertEquals("password", document.getElementById("password")?.attr("name"))
        assertEquals("password", document.getElementById("password")?.attr("type"))
        assertEquals(true, document.getElementById("password")?.hasAttr("required"))
        assertEquals(true, document.getElementsByAttributeValue("type", "submit").first()?.`is`("button"))
    }

    @Test
    fun testGetRegisterRoute() = testApplication {
        val client = installApp(this)
        val controller = mockk<IAuthController>()
        val getLocaleForCallUseCase = mockk<IGetLocaleForCallUseCase>()
        val router = AuthRouter(controller, getLocaleForCallUseCase)
        every { controller.register() } returns Unit
        every { getLocaleForCallUseCase(any()) } returns Locale.ENGLISH
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/en/auth/register")
        assertEquals(HttpStatusCode.OK, response.status)
        val document = Jsoup.parse(response.bodyAsText())
        assertEquals(true, document.getElementById("auth_register_title")?.`is`("h1"))
        assertEquals(true, document.getElementById("email")?.`is`("input"))
        assertEquals("email", document.getElementById("email")?.attr("name"))
        assertEquals("email", document.getElementById("email")?.attr("type"))
        assertEquals(true, document.getElementById("email")?.hasAttr("required"))
        assertEquals(true, document.getElementsByAttributeValue("type", "submit").first()?.`is`("button"))
    }

    @Test
    fun testGetRegisterCodeRoute() = testApplication {
        val client = installApp(this)
        val controller = mockk<AuthController>()
        val getLocaleForCallUseCase = mockk<IGetLocaleForCallUseCase>()
        val router = AuthRouter(controller, getLocaleForCallUseCase)
        coEvery { controller.registerCode(any(), "code") } returns RegisterPayload("email@email.com")
        every { getLocaleForCallUseCase(any()) } returns Locale.ENGLISH
        routing {
            router.createRoutes(this)
        }
        val response = client.get("/en/auth/register/code")
        assertEquals(HttpStatusCode.OK, response.status)
        val document = Jsoup.parse(response.bodyAsText())
        assertEquals(true, document.getElementById("auth_register_title")?.`is`("h1"))
        assertEquals(true, document.getElementById("username")?.`is`("input"))
        assertEquals("username", document.getElementById("username")?.attr("name"))
        assertEquals("text", document.getElementById("username")?.attr("type"))
        assertEquals(true, document.getElementById("username")?.hasAttr("required"))
        assertEquals(true, document.getElementById("displayName")?.`is`("input"))
        assertEquals("displayName", document.getElementById("displayName")?.attr("name"))
        assertEquals("text", document.getElementById("displayName")?.attr("type"))
        assertEquals(true, document.getElementById("displayName")?.hasAttr("required"))
        assertEquals(true, document.getElementById("email")?.`is`("input"))
        assertEquals("email", document.getElementById("email")?.attr("name"))
        assertEquals("email", document.getElementById("email")?.attr("type"))
        assertEquals("email@email.com", document.getElementById("email")?.attr("value"))
        assertEquals(true, document.getElementById("email")?.hasAttr("disabled"))
        assertEquals(true, document.getElementById("password")?.`is`("input"))
        assertEquals("password", document.getElementById("password")?.attr("name"))
        assertEquals("password", document.getElementById("password")?.attr("type"))
        assertEquals(true, document.getElementById("password")?.hasAttr("required"))
        assertEquals(true, document.getElementsByAttributeValue("type", "submit").first()?.`is`("button"))
    }

}

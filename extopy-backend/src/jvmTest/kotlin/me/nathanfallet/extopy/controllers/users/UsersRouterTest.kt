package me.nathanfallet.extopy.controllers.users

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.mockk
import me.nathanfallet.extopy.models.users.CreateUserPayload
import me.nathanfallet.extopy.models.users.UpdateUserPayload
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.extopy.plugins.Serialization
import me.nathanfallet.extopy.plugins.configureSerialization
import me.nathanfallet.ktorx.controllers.IModelController
import me.nathanfallet.usecases.models.UnitModel
import kotlin.test.Test
import kotlin.test.assertEquals

class UsersRouterTest {

    private val user = User("id", "displayName", "username")

    private fun installApp(application: ApplicationTestBuilder): HttpClient {
        application.environment {
            config = ApplicationConfig("application.test.conf")
        }
        application.application {
            configureSerialization()
        }
        return application.createClient {
            install(ContentNegotiation) {
                json(Serialization.json)
            }
        }
    }

    @Test
    fun testGetAPIv1() = testApplication {
        val client = installApp(this)
        val controller = mockk<IModelController<User, String, CreateUserPayload, UpdateUserPayload>>()
        val router = UsersRouter(controller)
        coEvery { controller.get(any(), UnitModel, "id") } returns user
        routing {
            router.createRoutes(this)
        }
        assertEquals(user, client.get("/api/v1/users/id").body())
    }

}

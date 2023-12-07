package me.nathanfallet.extopy.usecases.auth

import io.ktor.client.request.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.testing.*
import me.nathanfallet.extopy.models.auth.SessionPayload
import me.nathanfallet.extopy.plugins.configureSessions
import kotlin.test.Test
import kotlin.test.assertEquals

class SetSessionForCallUseCaseTest {

    @Test
    fun invoke() = testApplication {
        environment {
            config = ApplicationConfig("application.test.conf")
        }
        application {
            configureSessions()
        }
        routing {
            get {
                val useCase = SetSessionForCallUseCase()
                assertEquals(null, call.sessions.get<SessionPayload>())
                useCase(call, SessionPayload("id"))
                assertEquals(SessionPayload("id"), call.sessions.get<SessionPayload>())
            }
        }
        client.get("/")
    }

}

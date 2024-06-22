package com.extopy.usecases.auth

import io.ktor.client.request.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.testing.*
import com.extopy.models.auth.SessionPayload
import kotlin.test.Test
import kotlin.test.assertEquals

class GetSessionForCallUseCaseTest {

    @Test
    fun invoke() = testApplication {
        environment {
            config = ApplicationConfig("application.test.conf")
        }
        application {
            install(Sessions) {
                cookie<SessionPayload>("session", SessionStorageMemory())
            }
        }
        routing {
            get {
                val useCase = GetSessionForCallUseCase()
                assertEquals(null, useCase(call))
                call.sessions.set(SessionPayload("id"))
                assertEquals(SessionPayload("id"), useCase(call))
            }
        }
        client.get("/")
    }

}

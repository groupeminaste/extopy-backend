package com.extopy.plugins

import com.extopy.models.auth.SessionPayload
import dev.kaccelero.commons.sessions.ISessionsRepository
import io.ktor.server.application.*
import io.ktor.server.sessions.*
import org.koin.ktor.ext.inject

fun Application.configureSessions() {
    val sessionsRepository by inject<ISessionsRepository>()

    install(Sessions) {
        cookie<SessionPayload>("session", sessionsRepository) {
            cookie.path = "/"
        }
    }
}

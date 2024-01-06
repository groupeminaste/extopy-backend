package me.nathanfallet.extopy.plugins

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import me.nathanfallet.extopy.models.auth.SessionPayload
import me.nathanfallet.ktorx.repositories.sessions.ISessionsRepository
import org.koin.ktor.ext.inject

fun Application.configureSessions() {
    val sessionsRepository by inject<ISessionsRepository>()

    install(Sessions) {
        cookie<SessionPayload>("session", sessionsRepository) {
            cookie.path = "/"
        }
    }
}

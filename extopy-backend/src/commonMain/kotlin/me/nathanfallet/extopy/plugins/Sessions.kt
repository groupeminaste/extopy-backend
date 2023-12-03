package me.nathanfallet.extopy.plugins

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import me.nathanfallet.extopy.models.auth.SessionPayload

fun Application.configureSessions() {
    install(Sessions) {
        cookie<SessionPayload>("session", SessionStorageMemory()) {
            cookie.path = "/"
        }
    }
}

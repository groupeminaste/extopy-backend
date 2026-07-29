package com.extopy.presentation.config

import com.extopy.presentation.models.SessionPayload
import io.ktor.server.application.*
import io.ktor.server.sessions.*
import org.koin.ktor.ext.inject

fun Application.configureSessions() {
    val sessionStorage by inject<SessionStorage>()
    // Only a local run is served over plain http, where a secure cookie would never be sent back.
    val isLocal = environment.config.propertyOrNull("ktor.environment")?.getString()
        ?.let { it == "localhost" || it == "test" } ?: true

    install(Sessions) {
        cookie<SessionPayload>("session", sessionStorage) {
            cookie.path = "/"
            cookie.secure = !isLocal
            cookie.httpOnly = true
            cookie.extensions["SameSite"] = "Lax"
        }
    }
}

package com.extopy.presentation.config

import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.request.*
import org.slf4j.event.Level

fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
        // The probes run every thirty seconds on every instance, which is not worth a line each.
        filter { call ->
            call.request.path() != "/healthz" && call.request.path() != "/readyz"
        }
    }
}

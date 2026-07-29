package com.extopy.presentation.config

import com.extopy.presentation.errors.INTERNAL_ERROR
import com.extopy.presentation.errors.toHttpError
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*

/**
 * Configures error handling for the Ktor application.
 *
 * The failures the application knows about are listed in `presentation/errors/HttpErrors.kt`, which
 * the web forms read too; anything else is a bug, so it is logged and comes back as a 500.
 */
fun Application.configureErrorHandling() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            val error = cause.toHttpError() ?: run {
                call.application.log.error("Unhandled failure on ${call.request.path()}", cause)
                INTERNAL_ERROR
            }
            call.respond(error.status, mapOf("error" to error.key))
        }
    }
}

package com.extopy.infrastructure.config

import io.ktor.server.application.*
import io.ktor.server.application.hooks.*
import io.sentry.Sentry

/**
 * Hands Sentry whatever reached the top of the pipeline unhandled. The status pages turn the
 * failures the application knows about into a response, so this only ever sees the rest.
 */
private val SentryReporting = createApplicationPlugin("SentryReporting") {
    on(CallFailed) { _, cause ->
        Sentry.captureException(cause)
    }
}

/**
 * Reports the failures to Sentry, on the deployments that have one.
 *
 * A local or a test run has nothing to report to, so nothing is installed there and the SDK stays a
 * no-op.
 */
fun Application.configureSentry() {
    val env = environment.config.property("ktor.environment").getString()
        .takeIf { it != "localhost" && it != "test" } ?: return
    val dsn = environment.config.propertyOrNull("sentry.dsn")?.getString()?.takeIf { it.isNotEmpty() } ?: return

    Sentry.init { options ->
        options.dsn = dsn
        options.environment = env
        options.tracesSampleRate = when (env) {
            "production" -> PRODUCTION_TRACES_SAMPLE_RATE
            else -> 1.0
        }
    }
    install(SentryReporting)
}

private const val PRODUCTION_TRACES_SAMPLE_RATE = 0.1

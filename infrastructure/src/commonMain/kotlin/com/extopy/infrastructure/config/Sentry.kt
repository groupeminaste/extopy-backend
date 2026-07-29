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

    Sentry.init { options ->
        options.dsn = DSN
        options.environment = env
        options.tracesSampleRate = when (env) {
            "production" -> PRODUCTION_TRACES_SAMPLE_RATE
            else -> 1.0
        }
    }
    install(SentryReporting)
}

/**
 * Where the failures go. It names a project rather than granting anything, and it never changes, so
 * it lives here rather than in the configuration.
 */
private const val DSN =
    "https://0c402f45f26b3288980c13a4ef5db57a@o4506105040470016.ingest.sentry.io/4506370995453953"

private const val PRODUCTION_TRACES_SAMPLE_RATE = 0.1

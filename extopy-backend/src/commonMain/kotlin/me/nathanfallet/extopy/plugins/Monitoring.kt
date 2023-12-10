package me.nathanfallet.extopy.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.request.*
import io.sentry.Sentry
import me.nathanfallet.ktorx.plugins.KtorSentry
import org.slf4j.event.Level

fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    val env = environment.config.property("ktor.environment").getString().takeIf {
        it != "localhost" && it != "test"
    } ?: return
    Sentry.init {
        it.dsn = "https://0c402f45f26b3288980c13a4ef5db57a@o4506105040470016.ingest.sentry.io/4506370995453953"
        it.environment = env
        it.tracesSampleRate = when (env) {
            "production" -> 0.1
            "dev" -> 1.0
            else -> 0.0
        }
    }
    install(KtorSentry)
}

package com.extopy.presentation.routes

import com.extopy.presentation.extensions.SUPPORTED_LANGUAGES
import com.extopy.presentation.extensions.language
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Mounts [build] once per supported language, under `/en`, `/fr`, ...
 *
 * Everything under [prefix] without a language is answered with a redirect to the language the
 * visitor asked for, which is what keeps a plain `/auth/login` — what every link and every bookmark
 * points at — landing on a translated page.
 *
 * @param prefix The path the pages live under, e.g. `/auth`.
 */
fun Route.localized(prefix: String, build: Route.() -> Unit) {
    SUPPORTED_LANGUAGES.forEach { language ->
        route("/$language") { build() }
    }

    val redirect: suspend RoutingContext.() -> Unit = {
        call.respondRedirect("/${call.language()}${call.request.uri}")
    }
    route(prefix) { handle(redirect) }
    route("$prefix/{...}") { handle(redirect) }
}

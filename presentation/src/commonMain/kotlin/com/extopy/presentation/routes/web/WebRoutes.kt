package com.extopy.presentation.routes.web

import com.extopy.presentation.extensions.respondView
import io.ktor.server.routing.*

/**
 * Configures the landing page.
 *
 * It has nothing to say yet, and it is the one page that is not translated, so it is not mounted
 * under a language prefix.
 */
fun Route.webRoutes() {
    get("/") {
        call.respondView("index.ftl")
    }
}

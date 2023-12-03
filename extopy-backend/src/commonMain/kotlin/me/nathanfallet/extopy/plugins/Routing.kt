package me.nathanfallet.extopy.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        // Static files
        get("/") { call.respond(FreeMarkerContent("index.ftl", null)) }

        authenticate("api-v1-jwt", optional = true) {

        }

        staticResources("", "static")
    }
}

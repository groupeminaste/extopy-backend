package me.nathanfallet.extopy.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import me.nathanfallet.extopy.models.application.ExtopyJson

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(ExtopyJson.json)
    }
}

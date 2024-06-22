package com.extopy.plugins

import dev.kaccelero.serializers.Serialization
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Serialization.json)
    }
}

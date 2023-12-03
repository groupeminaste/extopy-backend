package me.nathanfallet.extopy.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

object Serialization {

    @OptIn(ExperimentalSerializationApi::class)
    val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Serialization.json)
    }
}

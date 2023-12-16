package me.nathanfallet.extopy.models.application

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

object ExtopyJson {

    @OptIn(ExperimentalSerializationApi::class)
    val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

}

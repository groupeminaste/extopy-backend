package com.extopy.api

import kotlinx.serialization.json.Json

/**
 * The JSON configuration both sides of the API agree on.
 */
object Serialization {

    /**
     * Unknown keys are ignored so that a client keeps working against a newer server, and nulls are
     * left out so that a payload only carries the fields that were actually read.
     */
    val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

}

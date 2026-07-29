package com.extopy.api.models.application

import dev.zodable.Zodable
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

/**
 * The deployments of Extopy a client can talk to.
 */
@Zodable
@JsExport
@Serializable
enum class ExtopyEnvironment {

    PRODUCTION, DEVELOPMENT;

    /**
     * The base URL of the API of this environment.
     */
    val baseUrl: String
        get() = when (this) {
            PRODUCTION -> "https://extopy.com"
            DEVELOPMENT -> "https://extopy.dev"
        }

}

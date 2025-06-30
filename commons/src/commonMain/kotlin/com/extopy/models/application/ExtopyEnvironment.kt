package com.extopy.models.application

import digital.guimauve.zodable.Zodable
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Zodable
@JsExport
@Serializable
enum class ExtopyEnvironment {

    PRODUCTION, DEVELOPMENT;

    val baseUrl: String
        get() = when (this) {
            PRODUCTION -> "https://extopy.com"
            DEVELOPMENT -> "https://extopy.dev"
        }

}

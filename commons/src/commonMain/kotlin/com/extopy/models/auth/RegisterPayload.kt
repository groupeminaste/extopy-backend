package com.extopy.models.auth

import digital.guimauve.zodable.Zodable
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Zodable
@JsExport
@Serializable
data class RegisterPayload(
    val email: String,
)

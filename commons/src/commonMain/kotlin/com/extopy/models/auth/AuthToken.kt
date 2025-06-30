package com.extopy.models.auth

import dev.kaccelero.annotations.Schema
import dev.kaccelero.models.UUID
import digital.guimauve.zodable.Zodable
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Zodable
@JsExport
@Serializable
data class AuthToken(
    @Schema(name = "Access Token", example = "1234abcd")
    val accessToken: String,
    @Schema(name = "Refresh Token", example = "1234abcd")
    val refreshToken: String,
    @Schema(name = "ID Token", example = "1234abcd")
    val idToken: UUID,
)

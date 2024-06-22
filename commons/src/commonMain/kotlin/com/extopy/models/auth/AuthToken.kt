package com.extopy.models.auth

import dev.kaccelero.annotations.Schema
import dev.kaccelero.models.UUID
import kotlinx.serialization.Serializable

@Serializable
data class AuthToken(
    @Schema(name = "Access Token", example = "1234abcd")
    val accessToken: String,
    @Schema(name = "Refresh Token", example = "1234abcd")
    val refreshToken: String,
    @Schema(name = "ID Token", example = "1234abcd")
    val idToken: UUID,
)

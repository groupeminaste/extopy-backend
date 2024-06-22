package com.extopy.models.auth

import dev.kaccelero.annotations.Schema
import dev.kaccelero.models.UUID
import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    @Schema("Client ID", "1234abcd")
    val clientId: UUID,
    @Schema("Client Secret", "1234abcd")
    val clientSecret: String,
    @Schema("Code", "1234abcd")
    val code: String,
    @Schema("Code Verifier", "1234abcd")
    val codeVerifier: String? = null,
)

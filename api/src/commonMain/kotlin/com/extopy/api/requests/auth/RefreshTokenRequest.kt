package com.extopy.api.requests.auth

import dev.zodable.Zodable
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

/**
 * The body a client sends to exchange a refresh token for a new token.
 */
@Zodable
@JsExport
@Serializable
data class RefreshTokenRequest(
    val refreshToken: String,
)

package com.extopy.api.responses.auth

import dev.zodable.Zodable
import kotlinx.serialization.Serializable
import kotlin.js.JsExport
import kotlin.uuid.Uuid

/**
 * The tokens the API hands out once a client is authorized.
 */
@Zodable
@JsExport
@Serializable
data class AuthTokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val idToken: Uuid,
)

package com.extopy.client.auth

import kotlinx.serialization.Serializable

/**
 * The pair a client holds on to once it has been authorized.
 */
@Serializable
data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
)

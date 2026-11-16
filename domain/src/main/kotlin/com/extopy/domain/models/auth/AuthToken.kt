package com.extopy.domain.models.auth

import kotlin.uuid.Uuid

/**
 * The tokens handed to a client once it is authorized.
 */
data class AuthToken(
    val accessToken: String,
    val refreshToken: String,
    val idToken: Uuid,
)

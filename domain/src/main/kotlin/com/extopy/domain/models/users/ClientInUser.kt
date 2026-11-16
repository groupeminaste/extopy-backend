package com.extopy.domain.models.users

import kotlin.time.Instant
import kotlin.uuid.Uuid

/**
 * The authorization code a user handed to a client, waiting to be exchanged for a token.
 */
data class ClientInUser(
    val code: String,
    val userId: Uuid,
    val clientId: Uuid,
    val expiresAt: Instant,
)

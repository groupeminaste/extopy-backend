package com.extopy.domain.models.notifications

import com.extopy.domain.models.users.User
import kotlin.time.Instant
import kotlin.uuid.Uuid

/**
 * A device a user is reachable on, as the client that registered it named it.
 *
 * Note: nothing writes or reads these yet, see [Notification].
 */
data class TokenInNotification(
    val token: String,
    val service: String,
    val clientId: Uuid,
    val userId: Uuid,
    val expiresAt: Instant,
    val user: User? = null,
)

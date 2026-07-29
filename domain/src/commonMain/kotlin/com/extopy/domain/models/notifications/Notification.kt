package com.extopy.domain.models.notifications

import kotlin.time.Instant
import kotlin.uuid.Uuid

/**
 * Something that happened which a user is told about.
 *
 * Note: nothing writes or reads these yet. The push notifications were started and not finished, and
 * this is the shape they were being built around.
 */
data class Notification(
    val id: Uuid,
    val userId: Uuid,
    val type: String,
    val body: String,
    val contentId: Uuid? = null,
    val publishedAt: Instant? = null,
    val expiresAt: Instant? = null,
    val read: Boolean? = null,
)

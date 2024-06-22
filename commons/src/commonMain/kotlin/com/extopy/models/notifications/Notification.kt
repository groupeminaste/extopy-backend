package com.extopy.models.notifications

import dev.kaccelero.models.UUID
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val id: UUID,
    val userId: UUID?,
    val type: String?,
    val body: String?,
    val contentId: UUID?,
    val publishedAt: Instant? = null,
    val expiresAt: Instant? = null,
    val read: Boolean? = null,
)

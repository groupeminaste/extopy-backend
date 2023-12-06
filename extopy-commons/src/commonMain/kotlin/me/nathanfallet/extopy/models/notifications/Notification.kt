package me.nathanfallet.extopy.models.notifications

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val id: String,
    val userId: String?,
    val type: String?,
    val body: String?,
    val contentId: String?,
    val published: Instant? = null,
    val expiration: Instant? = null,
    val read: Boolean? = null,
)

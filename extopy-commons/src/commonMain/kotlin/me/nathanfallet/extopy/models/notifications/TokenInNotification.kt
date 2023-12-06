package me.nathanfallet.extopy.models.notifications

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import me.nathanfallet.extopy.models.users.User

@Serializable
data class TokenInNotification(
    val token: String,
    val service: String,
    val clientId: String,
    val userId: String,
    val expiration: Instant,
    val user: User? = null,
)

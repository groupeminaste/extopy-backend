package me.nathanfallet.extopy.models.auth

import kotlinx.serialization.Serializable

@Serializable
data class SessionPayload(
    val userId: String,
)

package me.nathanfallet.extopy.models.users

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserPayload(
    val username: String,
    val displayName: String,
    val email: String,
    val password: String,
)

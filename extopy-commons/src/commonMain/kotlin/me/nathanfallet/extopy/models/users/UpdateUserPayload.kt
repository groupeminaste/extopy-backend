package me.nathanfallet.extopy.models.users

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserPayload(
    val username: String?,
    val displayName: String?,
    val biography: String?,
    val avatar: String?,
    val personal: Boolean?,
)

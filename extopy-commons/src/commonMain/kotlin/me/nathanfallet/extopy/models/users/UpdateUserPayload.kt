package me.nathanfallet.extopy.models.users

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserPayload(
    val username: String? = null,
    val displayName: String? = null,
    val biography: String? = null,
    val avatar: String? = null,
    val personal: Boolean? = null,
)

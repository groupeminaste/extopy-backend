package com.extopy.domain.models.users

/**
 * The fields of an account a user may change. Anything left null is kept as it is.
 */
data class UpdateUserPayload(
    val username: String? = null,
    val displayName: String? = null,
    val password: String? = null,
    val biography: String? = null,
    val avatar: String? = null,
    val personal: Boolean? = null,
)

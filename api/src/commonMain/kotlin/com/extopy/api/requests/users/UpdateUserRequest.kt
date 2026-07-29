package com.extopy.api.requests.users

import dev.zodable.Zodable
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

/**
 * The body a client sends to update a user. Every field left out is kept as it is.
 */
@Zodable
@JsExport
@Serializable
data class UpdateUserRequest(
    val username: String? = null,
    val displayName: String? = null,
    val password: String? = null,
    val biography: String? = null,
    val avatar: String? = null,
    val personal: Boolean? = null,
)

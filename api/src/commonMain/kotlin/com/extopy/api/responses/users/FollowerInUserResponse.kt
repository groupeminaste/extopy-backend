package com.extopy.api.responses.users

import dev.zodable.Zodable
import kotlinx.serialization.Serializable
import kotlin.js.JsExport
import kotlin.uuid.Uuid

/**
 * A follow link between two users, as the API exposes it.
 */
@Zodable
@JsExport
@Serializable
data class FollowerInUserResponse(
    val userId: Uuid,
    val targetId: Uuid,
    val accepted: Boolean? = null,
    val user: UserResponse? = null,
    val target: UserResponse? = null,
)

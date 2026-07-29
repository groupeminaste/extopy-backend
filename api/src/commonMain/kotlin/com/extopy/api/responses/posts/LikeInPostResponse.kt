package com.extopy.api.responses.posts

import com.extopy.api.responses.users.UserResponse
import dev.zodable.Zodable
import kotlinx.serialization.Serializable
import kotlin.js.JsExport
import kotlin.uuid.Uuid

/**
 * The like of a user on a post, as the API exposes it.
 */
@Zodable
@JsExport
@Serializable
data class LikeInPostResponse(
    val postId: Uuid,
    val userId: Uuid,
    val post: PostResponse? = null,
    val user: UserResponse? = null,
)

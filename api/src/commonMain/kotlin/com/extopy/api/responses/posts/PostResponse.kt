package com.extopy.api.responses.posts

import com.extopy.api.responses.users.UserResponse
import dev.zodable.Zodable
import kotlinx.serialization.Serializable
import kotlin.js.JsExport
import kotlin.time.Instant
import kotlin.uuid.Uuid

/**
 * A post, as the API exposes it.
 */
@Zodable
@JsExport
@Serializable
data class PostResponse(
    val id: Uuid,
    val userId: Uuid,
    val user: UserResponse? = null,
    val repliedToId: Uuid? = null,
    val repostOfId: Uuid? = null,
    val body: String? = null,
    val publishedAt: Instant,
    val editedAt: Instant? = null,
    val expiresAt: Instant? = null,
    val visibility: String? = null,
    val likesCount: Long? = null,
    val repliesCount: Long? = null,
    val repostsCount: Long? = null,
    val likesIn: Boolean? = null,
)

package com.extopy.api.requests.posts

import dev.zodable.Zodable
import kotlinx.serialization.Serializable
import kotlin.js.JsExport
import kotlin.uuid.Uuid

/**
 * The body a client sends to create or update a post.
 */
@Zodable
@JsExport
@Serializable
data class PostRequest(
    val body: String,
    val repliedToId: Uuid? = null,
    val repostOfId: Uuid? = null,
)

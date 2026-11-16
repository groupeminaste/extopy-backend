package com.extopy.domain.models.posts

import kotlin.uuid.Uuid

/**
 * What a user writes when publishing or editing a post.
 */
data class PostPayload(
    val body: String,
    val repliedToId: Uuid? = null,
    val repostOfId: Uuid? = null,
)

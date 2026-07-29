package com.extopy.domain.models.posts

import com.extopy.domain.models.users.User
import kotlin.uuid.Uuid

/**
 * The like of a user on a post.
 */
data class LikeInPost(
    val postId: Uuid,
    val userId: Uuid,
    val post: Post? = null,
    val user: User? = null,
)

package me.nathanfallet.extopy.models.posts

import kotlinx.serialization.Serializable
import me.nathanfallet.extopy.models.users.User

@Serializable
data class LikeInPost(
    val postId: String,
    val userId: String,
    val post: Post?,
    val user: User?,
)

package me.nathanfallet.extopy.models

import kotlinx.serialization.Serializable
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.users.User

@Serializable
data class Timeline(
    val id: String? = null,
    val type: String,
    val user: User? = null,
    val post: Post? = null,
    val users: List<User>? = null,
    val posts: List<Post>? = null,
)

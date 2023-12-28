package me.nathanfallet.extopy.models.posts

import kotlinx.serialization.Serializable
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.usecases.models.IChildModel

@Serializable
data class LikeInPost(
    val postId: String,
    val userId: String,
    val post: Post?,
    val user: User?,
) : IChildModel<String, Unit, Unit, String> {

    override val id = userId
    override val parentId = postId

}

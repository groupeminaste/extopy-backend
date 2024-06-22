package com.extopy.models.posts

import kotlinx.serialization.Serializable
import com.extopy.models.users.User
import dev.kaccelero.models.IChildModel

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

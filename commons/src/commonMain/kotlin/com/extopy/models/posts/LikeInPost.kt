package com.extopy.models.posts

import com.extopy.models.users.User
import dev.kaccelero.models.IChildModel
import dev.kaccelero.models.UUID
import digital.guimauve.zodable.Zodable
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Zodable
@JsExport
@Serializable
data class LikeInPost(
    val postId: UUID,
    val userId: UUID,
    val post: Post?,
    val user: User?,
) : IChildModel<UUID, Unit, Unit, UUID> {

    override val id: UUID
        get() = userId

    override val parentId: UUID
        get() = postId

}

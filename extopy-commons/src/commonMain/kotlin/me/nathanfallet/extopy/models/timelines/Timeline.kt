package me.nathanfallet.extopy.models.timelines

import kotlinx.serialization.Serializable
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.usecases.models.IModel
import me.nathanfallet.usecases.models.annotations.Schema

// Note: For now we only support default timeline, but we will add more later (custom timelines)
@Serializable
data class Timeline(
    @Schema("Id of the Timeline", "123abc")
    override val id: String,
    @Schema("Type of the Timeline", "default")
    val type: String,
    @Schema("Users returned for this timeline", "[]")
    val users: List<User>? = null,
    @Schema("Posts returned for this timeline", "[]")
    val posts: List<Post>? = null,
) : IModel<String, Unit, Unit>

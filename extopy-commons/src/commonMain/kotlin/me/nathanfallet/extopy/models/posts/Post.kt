package me.nathanfallet.extopy.models.posts

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.usecases.models.IModel

@Serializable
data class Post(
    override val id: String,
    val userId: String?,
    val user: User?,
    val repliedToId: String?,
    val repostOfId: String?,
    val body: String?,
    val published: Instant?,
    val edited: Instant?,
    val expiration: Instant?,
    val visibility: String?,
    val likesCount: Long?,
    val repliesCount: Long?,
    val repostsCount: Long?,
    val likesIn: Boolean?,
) : IModel<String, PostPayload, PostPayload>

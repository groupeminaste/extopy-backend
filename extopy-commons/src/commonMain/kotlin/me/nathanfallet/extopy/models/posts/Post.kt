package me.nathanfallet.extopy.models.posts

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.usecases.models.IModel

@Serializable
data class Post(
    override val id: String,
    val userId: String? = null,
    val user: User? = null,
    val repliedToId: String? = null,
    val repostOfId: String? = null,
    val body: String? = null,
    val published: Instant? = null,
    val edited: Instant? = null,
    val expiration: Instant? = null,
    val visibility: String? = null,
    val likesCount: Long? = null,
    val repliesCount: Long? = null,
    val repostsCount: Long? = null,
    val likesIn: Boolean? = null,
) : IModel<String, PostPayload, PostPayload>

package me.nathanfallet.extopy.models.posts

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.usecases.models.IModel
import me.nathanfallet.usecases.models.annotations.Schema

@Serializable
data class Post(
    @Schema("Id of the Post", "123abc")
    override val id: String,
    @Schema("Id of the User who posted", "123abc")
    val userId: String? = null,
    @Schema("User who posted")
    val user: User? = null,
    @Schema("Id of the Post replied to", "123abc")
    val repliedToId: String? = null,
    @Schema("Id of the Post reposted", "123abc")
    val repostOfId: String? = null,
    @Schema("Body of the Post", "Hello world!")
    val body: String? = null,
    @Schema("Date of the Post", "2023-12-13T09:41:00Z")
    val published: Instant? = null,
    @Schema("Date of the Post last edited, if edited", "2023-12-13T09:41:00Z")
    val edited: Instant? = null,
    @Schema("Date of the Post expiration, if set", "2023-12-13T09:41:00Z")
    val expiration: Instant? = null,
    @Schema("Visibility of the Post", "public")
    val visibility: String? = null,
    @Schema("Number of likes of the Post", "123")
    val likesCount: Long? = null,
    @Schema("Number of replies of the Post", "123")
    val repliesCount: Long? = null,
    @Schema("Number of reposts of the Post", "123")
    val repostsCount: Long? = null,
    @Schema("Is the current user has liked this post?", "true")
    val likesIn: Boolean? = null,
) : IModel<String, PostPayload, PostPayload>

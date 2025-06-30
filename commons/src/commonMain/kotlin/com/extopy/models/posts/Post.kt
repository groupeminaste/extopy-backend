package com.extopy.models.posts

import com.extopy.models.users.User
import dev.kaccelero.annotations.Schema
import dev.kaccelero.models.IModel
import dev.kaccelero.models.UUID
import digital.guimauve.zodable.Zodable
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Zodable
@JsExport
@Serializable
data class Post(
    @Schema("Id of the Post", "123abc")
    override val id: UUID,
    @Schema("Id of the User who posted", "123abc")
    val userId: UUID,
    @Schema("User who posted")
    val user: User? = null,
    @Schema("Id of the Post replied to", "123abc")
    val repliedToId: UUID? = null,
    @Schema("Id of the Post reposted", "123abc")
    val repostOfId: UUID? = null,
    @Schema("Body of the Post", "Hello world!")
    val body: String? = null,
    @Schema("Date of the Post", "2023-12-13T09:41:00Z")
    val publishedAt: Instant,
    @Schema("Date of the Post last edited, if edited", "2023-12-13T09:41:00Z")
    val editedAt: Instant? = null,
    @Schema("Date of the Post expiration, if set", "2023-12-13T09:41:00Z")
    val expiresAt: Instant? = null,
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
) : IModel<UUID, PostPayload, PostPayload>

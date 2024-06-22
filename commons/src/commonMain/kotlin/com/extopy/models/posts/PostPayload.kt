package com.extopy.models.posts

import dev.kaccelero.annotations.Schema
import dev.kaccelero.annotations.StringPropertyValidator
import dev.kaccelero.models.UUID
import kotlinx.serialization.Serializable

@Serializable
data class PostPayload(
    @StringPropertyValidator(maxLength = 20_000)
    @Schema("Body of the Post", "Hello world!")
    val body: String,
    @Schema("Id of the Post replied to", "123abc")
    val repliedToId: UUID? = null,
    @Schema("Id of the Post reposted", "123abc")
    val repostOfId: UUID? = null,
)

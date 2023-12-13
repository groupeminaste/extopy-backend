package me.nathanfallet.extopy.models.posts

import kotlinx.serialization.Serializable
import me.nathanfallet.usecases.models.annotations.Schema
import me.nathanfallet.usecases.models.annotations.validators.StringPropertyValidator

@Serializable
data class PostPayload(
    @StringPropertyValidator(maxLength = 20_000)
    @Schema("Body of the Post", "Hello world!")
    val body: String,
    @Schema("Id of the Post replied to", "123abc")
    val repliedToId: String? = null,
    @Schema("Id of the Post reposted", "123abc")
    val repostOfId: String? = null,
)

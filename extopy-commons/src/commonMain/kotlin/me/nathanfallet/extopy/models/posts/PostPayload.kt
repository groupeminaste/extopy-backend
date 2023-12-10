package me.nathanfallet.extopy.models.posts

import kotlinx.serialization.Serializable
import me.nathanfallet.usecases.models.annotations.validators.StringPropertyValidator

@Serializable
data class PostPayload(
    @StringPropertyValidator(maxLength = 20_000)
    val body: String,
    val repliedToId: String? = null,
    val repostOfId: String? = null,
)

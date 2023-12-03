package me.nathanfallet.extopy.models.posts

import kotlinx.serialization.Serializable

@Serializable
data class PostPayload(val body: String, val repliedToId: String?, val repostOfId: String?)

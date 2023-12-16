package me.nathanfallet.extopy.repositories.posts

import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.posts.PostPayload

interface IPostsRemoteRepository {

    suspend fun get(id: String): Post?
    suspend fun create(payload: PostPayload): Post?
    suspend fun update(id: String, payload: PostPayload): Post?
    suspend fun delete(id: String): Boolean

}

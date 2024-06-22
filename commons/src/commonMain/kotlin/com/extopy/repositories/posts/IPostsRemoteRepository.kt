package com.extopy.repositories.posts

import com.extopy.models.posts.Post
import com.extopy.models.posts.PostPayload
import dev.kaccelero.repositories.Pagination

interface IPostsRemoteRepository {

    suspend fun list(pagination: Pagination): List<Post>
    suspend fun get(id: String): Post?
    suspend fun create(payload: PostPayload): Post?
    suspend fun update(id: String, payload: PostPayload): Post?
    suspend fun delete(id: String): Boolean
    suspend fun getReplies(id: String, pagination: Pagination): List<Post>

}

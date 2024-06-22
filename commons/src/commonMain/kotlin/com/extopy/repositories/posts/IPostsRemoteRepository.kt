package com.extopy.repositories.posts

import com.extopy.models.posts.Post
import com.extopy.models.posts.PostPayload
import dev.kaccelero.models.UUID
import dev.kaccelero.repositories.Pagination

interface IPostsRemoteRepository {

    suspend fun list(pagination: Pagination): List<Post>
    suspend fun get(id: UUID): Post?
    suspend fun create(payload: PostPayload): Post?
    suspend fun update(id: UUID, payload: PostPayload): Post?
    suspend fun delete(id: UUID): Boolean
    suspend fun getReplies(id: UUID, pagination: Pagination): List<Post>

}

package com.extopy.repositories.users

import com.extopy.models.posts.Post
import com.extopy.models.users.UpdateUserPayload
import com.extopy.models.users.User
import dev.kaccelero.models.UUID
import dev.kaccelero.repositories.Pagination

interface IUsersRemoteRepository {

    suspend fun list(pagination: Pagination): List<User>
    suspend fun get(id: UUID): User?
    suspend fun update(id: UUID, payload: UpdateUserPayload): User?
    suspend fun getPosts(id: UUID, pagination: Pagination): List<Post>

}

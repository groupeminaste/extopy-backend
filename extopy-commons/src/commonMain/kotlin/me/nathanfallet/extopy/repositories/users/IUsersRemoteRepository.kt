package me.nathanfallet.extopy.repositories.users

import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.users.UpdateUserPayload
import me.nathanfallet.extopy.models.users.User

interface IUsersRemoteRepository {

    suspend fun get(id: String): User?
    suspend fun update(id: String, payload: UpdateUserPayload): User?
    suspend fun getPosts(id: String, limit: Long, offset: Long): List<Post>

}

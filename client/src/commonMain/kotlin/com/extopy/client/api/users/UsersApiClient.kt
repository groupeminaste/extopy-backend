package com.extopy.client.api.users

import com.extopy.api.requests.users.UpdateUserRequest
import com.extopy.api.responses.posts.PostResponse
import com.extopy.api.responses.users.UserResponse
import kotlin.uuid.Uuid

/**
 * The users endpoints of the API.
 */
interface UsersApiClient {

    suspend fun list(limit: Long? = null, offset: Long? = null, search: String? = null): List<UserResponse>

    suspend fun get(id: Uuid): UserResponse

    suspend fun update(id: Uuid, request: UpdateUserRequest): UserResponse

    suspend fun listPosts(id: Uuid, limit: Long? = null, offset: Long? = null): List<PostResponse>

}

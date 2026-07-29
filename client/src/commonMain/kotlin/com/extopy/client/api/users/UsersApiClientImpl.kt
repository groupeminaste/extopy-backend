package com.extopy.client.api.users

import com.extopy.api.requests.users.UpdateUserRequest
import com.extopy.api.resources.users.UsersApi
import com.extopy.api.responses.posts.PostResponse
import com.extopy.api.responses.users.UserResponse
import com.extopy.client.api.paginate
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlin.uuid.Uuid

class UsersApiClientImpl(
    private val client: HttpClient,
) : UsersApiClient {

    override suspend fun list(limit: Long?, offset: Long?, search: String?): List<UserResponse> = client
        .get(UsersApi()) { paginate(limit, offset, search) }
        .body()

    override suspend fun get(id: Uuid): UserResponse = client
        .get(UsersApi.Id(id))
        .body()

    override suspend fun update(id: Uuid, request: UpdateUserRequest): UserResponse = client
        .put(UsersApi.Id(id)) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        .body()

    override suspend fun listPosts(id: Uuid, limit: Long?, offset: Long?): List<PostResponse> = client
        .get(UsersApi.Id.Posts(UsersApi.Id(id))) { paginate(limit, offset) }
        .body()

}

package com.extopy.client.api.posts

import com.extopy.api.requests.posts.PostRequest
import com.extopy.api.resources.posts.PostsApi
import com.extopy.api.responses.posts.PostResponse
import com.extopy.client.api.paginate
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlin.uuid.Uuid

class PostsApiClientImpl(
    private val client: HttpClient,
) : PostsApiClient {

    override suspend fun list(limit: Long?, offset: Long?, search: String?): List<PostResponse> = client
        .get(PostsApi()) { paginate(limit, offset, search) }
        .body()

    override suspend fun get(id: Uuid): PostResponse = client
        .get(PostsApi.Id(id))
        .body()

    override suspend fun create(request: PostRequest): PostResponse = client
        .post(PostsApi()) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        .body()

    override suspend fun update(id: Uuid, request: PostRequest): PostResponse = client
        .put(PostsApi.Id(id)) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        .body()

    override suspend fun delete(id: Uuid) {
        client.delete(PostsApi.Id(id))
    }

    override suspend fun listReplies(id: Uuid, limit: Long?, offset: Long?): List<PostResponse> = client
        .get(PostsApi.Id.Replies(PostsApi.Id(id))) { paginate(limit, offset) }
        .body()

}

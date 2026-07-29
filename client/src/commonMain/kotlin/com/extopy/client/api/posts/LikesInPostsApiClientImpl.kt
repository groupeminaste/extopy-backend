package com.extopy.client.api.posts

import com.extopy.api.resources.posts.LikesInPostsApi
import com.extopy.api.responses.posts.LikeInPostResponse
import com.extopy.client.api.paginate
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import kotlin.uuid.Uuid

class LikesInPostsApiClientImpl(
    private val client: HttpClient,
) : LikesInPostsApiClient {

    override suspend fun list(postId: Uuid, limit: Long?, offset: Long?): List<LikeInPostResponse> = client
        .get(LikesInPostsApi(postId)) { paginate(limit, offset) }
        .body()

    override suspend fun create(postId: Uuid): LikeInPostResponse = client
        .post(LikesInPostsApi(postId))
        .body()

    override suspend fun delete(postId: Uuid, userId: Uuid) {
        client.delete(LikesInPostsApi.Id(userId, LikesInPostsApi(postId)))
    }

}

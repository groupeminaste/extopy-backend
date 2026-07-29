package com.extopy.client.api.posts

import com.extopy.api.requests.posts.PostRequest
import com.extopy.api.responses.posts.PostResponse
import kotlin.uuid.Uuid

/**
 * The posts endpoints of the API.
 */
interface PostsApiClient {

    suspend fun list(limit: Long? = null, offset: Long? = null, search: String? = null): List<PostResponse>

    suspend fun get(id: Uuid): PostResponse

    suspend fun create(request: PostRequest): PostResponse

    suspend fun update(id: Uuid, request: PostRequest): PostResponse

    suspend fun delete(id: Uuid)

    suspend fun listReplies(id: Uuid, limit: Long? = null, offset: Long? = null): List<PostResponse>

}

package com.extopy.client.api.posts

import com.extopy.api.responses.posts.LikeInPostResponse
import kotlin.uuid.Uuid

/**
 * The likes endpoints of the API.
 */
interface LikesInPostsApiClient {

    suspend fun list(postId: Uuid, limit: Long? = null, offset: Long? = null): List<LikeInPostResponse>

    suspend fun create(postId: Uuid): LikeInPostResponse

    suspend fun delete(postId: Uuid, userId: Uuid)

}

package com.extopy.client.api.timelines

import com.extopy.api.responses.posts.PostResponse
import com.extopy.api.responses.timelines.TimelineResponse
import kotlin.uuid.Uuid

/**
 * The timelines endpoints of the API.
 */
interface TimelinesApiClient {

    suspend fun get(id: Uuid): TimelineResponse

    suspend fun listPosts(id: Uuid, limit: Long? = null, offset: Long? = null): List<PostResponse>

}

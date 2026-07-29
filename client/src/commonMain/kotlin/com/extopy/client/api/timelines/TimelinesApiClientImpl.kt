package com.extopy.client.api.timelines

import com.extopy.api.resources.timelines.TimelinesApi
import com.extopy.api.responses.posts.PostResponse
import com.extopy.api.responses.timelines.TimelineResponse
import com.extopy.client.api.paginate
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import kotlin.uuid.Uuid

class TimelinesApiClientImpl(
    private val client: HttpClient,
) : TimelinesApiClient {

    override suspend fun get(id: Uuid): TimelineResponse = client
        .get(TimelinesApi.Id(id))
        .body()

    override suspend fun listPosts(id: Uuid, limit: Long?, offset: Long?): List<PostResponse> = client
        .get(TimelinesApi.Id.Posts(TimelinesApi.Id(id))) { paginate(limit, offset) }
        .body()

}

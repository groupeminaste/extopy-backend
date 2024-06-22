package com.extopy.repositories.timelines

import com.extopy.client.IExtopyClient
import com.extopy.models.posts.Post
import com.extopy.models.timelines.Timeline
import dev.kaccelero.models.RecursiveId
import dev.kaccelero.models.UnitModel
import dev.kaccelero.repositories.APIModelRemoteRepository
import dev.kaccelero.repositories.Pagination
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.reflect.*

class TimelinesRemoteRepository(
    client: IExtopyClient,
) : APIModelRemoteRepository<Timeline, String, Unit, Unit>(
    typeInfo<Timeline>(),
    typeInfo<Unit>(),
    typeInfo<Unit>(),
    typeInfo<List<Timeline>>(),
    client,
    prefix = "/api/v1"
), ITimelinesRemoteRepository {

    override suspend fun get(id: String): Timeline? {
        return get(id, null)
    }

    override suspend fun getPosts(id: String, pagination: Pagination): List<Post> {
        return client
            .request(HttpMethod.Get, "${constructFullRoute(RecursiveId<UnitModel, Unit, Unit>(Unit))}/$id/posts") {
                parameter("limit", pagination.limit)
                parameter("offset", pagination.offset)
            }
            .body()
    }

}

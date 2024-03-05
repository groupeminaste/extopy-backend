package me.nathanfallet.extopy.repositories.timelines

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.reflect.*
import me.nathanfallet.extopy.client.IExtopyClient
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.timelines.Timeline
import me.nathanfallet.ktorx.repositories.api.APIModelRemoteRepository
import me.nathanfallet.usecases.models.UnitModel
import me.nathanfallet.usecases.models.id.RecursiveId
import me.nathanfallet.usecases.pagination.Pagination

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

package com.extopy.repositories.posts

import com.extopy.client.IExtopyClient
import com.extopy.models.application.SearchOptions
import com.extopy.models.posts.Post
import com.extopy.models.posts.PostPayload
import dev.kaccelero.models.RecursiveId
import dev.kaccelero.models.UUID
import dev.kaccelero.models.UnitModel
import dev.kaccelero.repositories.APIModelRemoteRepository
import dev.kaccelero.repositories.IPaginationOptions
import dev.kaccelero.repositories.Pagination
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.reflect.*

class PostsRemoteRepository(
    client: IExtopyClient,
) : APIModelRemoteRepository<Post, UUID, PostPayload, PostPayload>(
    typeInfo<Post>(),
    typeInfo<PostPayload>(),
    typeInfo<PostPayload>(),
    typeInfo<List<Post>>(),
    client,
    prefix = "/api/v1"
), IPostsRemoteRepository {

    override fun encodePaginationOptions(options: IPaginationOptions, builder: HttpRequestBuilder) = when (options) {
        is SearchOptions -> builder.parameter("search", options.search)
        else -> super.encodePaginationOptions(options, builder)
    }

    override suspend fun list(pagination: Pagination): List<Post> = list(pagination, null)

    override suspend fun get(id: UUID): Post? = get(id, null)

    override suspend fun create(payload: PostPayload): Post? = create(payload, null)

    override suspend fun update(id: UUID, payload: PostPayload): Post? = update(id, payload, null)

    override suspend fun delete(id: UUID): Boolean = delete(id, null)

    override suspend fun getReplies(id: UUID, pagination: Pagination): List<Post> =
        client
            .request(HttpMethod.Get, "${constructFullRoute(RecursiveId<UnitModel, Unit, Unit>(Unit))}/$id/replies") {
                parameter("limit", pagination.limit)
                parameter("offset", pagination.offset)
            }
            .body()

}

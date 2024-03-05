package me.nathanfallet.extopy.repositories.posts

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.reflect.*
import me.nathanfallet.extopy.client.IExtopyClient
import me.nathanfallet.extopy.models.application.SearchOptions
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.posts.PostPayload
import me.nathanfallet.ktorx.repositories.api.APIModelRemoteRepository
import me.nathanfallet.usecases.models.UnitModel
import me.nathanfallet.usecases.models.id.RecursiveId
import me.nathanfallet.usecases.pagination.IPaginationOptions
import me.nathanfallet.usecases.pagination.Pagination

class PostsRemoteRepository(
    client: IExtopyClient,
) : APIModelRemoteRepository<Post, String, PostPayload, PostPayload>(
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

    override suspend fun get(id: String): Post? = get(id, null)

    override suspend fun create(payload: PostPayload): Post? = create(payload, null)

    override suspend fun update(id: String, payload: PostPayload): Post? = update(id, payload, null)

    override suspend fun delete(id: String): Boolean = delete(id, null)

    override suspend fun getReplies(id: String, pagination: Pagination): List<Post> =
        client
            .request(HttpMethod.Get, "${constructFullRoute(RecursiveId<UnitModel, Unit, Unit>(Unit))}/$id/replies") {
                parameter("limit", pagination.limit)
                parameter("offset", pagination.offset)
            }
            .body()

}

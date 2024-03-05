package me.nathanfallet.extopy.repositories.users

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.reflect.*
import me.nathanfallet.extopy.client.IExtopyClient
import me.nathanfallet.extopy.models.application.SearchOptions
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.users.CreateUserPayload
import me.nathanfallet.extopy.models.users.UpdateUserPayload
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.ktorx.repositories.api.APIModelRemoteRepository
import me.nathanfallet.usecases.models.UnitModel
import me.nathanfallet.usecases.models.id.RecursiveId
import me.nathanfallet.usecases.pagination.IPaginationOptions
import me.nathanfallet.usecases.pagination.Pagination

class UsersRemoteRepository(
    client: IExtopyClient,
) : APIModelRemoteRepository<User, String, CreateUserPayload, UpdateUserPayload>(
    typeInfo<User>(),
    typeInfo<CreateUserPayload>(),
    typeInfo<UpdateUserPayload>(),
    typeInfo<List<User>>(),
    client,
    prefix = "/api/v1"
), IUsersRemoteRepository {

    override fun encodePaginationOptions(options: IPaginationOptions, builder: HttpRequestBuilder) = when (options) {
        is SearchOptions -> builder.parameter("search", options.search)
        else -> super.encodePaginationOptions(options, builder)
    }

    override suspend fun list(pagination: Pagination): List<User> = list(pagination, null)

    override suspend fun get(id: String): User? = get(id, null)

    override suspend fun update(id: String, payload: UpdateUserPayload): User? = update(id, payload, null)

    override suspend fun getPosts(id: String, pagination: Pagination): List<Post> =
        client
            .request(HttpMethod.Get, "${constructFullRoute(RecursiveId<UnitModel, Unit, Unit>(Unit))}/$id/posts") {
                parameter("limit", pagination.limit)
                parameter("offset", pagination.offset)
            }
            .body()

}

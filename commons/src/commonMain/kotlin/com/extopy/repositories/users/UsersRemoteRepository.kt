package com.extopy.repositories.users

import com.extopy.client.IExtopyClient
import com.extopy.models.application.SearchOptions
import com.extopy.models.posts.Post
import com.extopy.models.users.CreateUserPayload
import com.extopy.models.users.UpdateUserPayload
import com.extopy.models.users.User
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

class UsersRemoteRepository(
    client: IExtopyClient,
) : APIModelRemoteRepository<User, UUID, CreateUserPayload, UpdateUserPayload>(
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

    override suspend fun get(id: UUID): User? = get(id, null)

    override suspend fun update(id: UUID, payload: UpdateUserPayload): User? = update(id, payload, null)

    override suspend fun getPosts(id: UUID, pagination: Pagination): List<Post> = client
        .request(HttpMethod.Get, "${constructFullRoute(RecursiveId<UnitModel, Unit, Unit>(Unit))}/$id/posts") {
            parameter("limit", pagination.limit)
            parameter("offset", pagination.offset)
        }
        .body()

}

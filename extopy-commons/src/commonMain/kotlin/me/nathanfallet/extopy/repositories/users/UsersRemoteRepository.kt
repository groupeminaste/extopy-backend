package me.nathanfallet.extopy.repositories.users

import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.util.reflect.*
import me.nathanfallet.extopy.client.IExtopyClient
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.users.CreateUserPayload
import me.nathanfallet.extopy.models.users.UpdateUserPayload
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.ktorx.repositories.api.APIModelRemoteRepository
import me.nathanfallet.usecases.models.UnitModel
import me.nathanfallet.usecases.models.id.RecursiveId

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

    override suspend fun get(id: String): User? {
        return get(id, null)
    }

    override suspend fun update(id: String, payload: UpdateUserPayload): User? {
        return update(id, payload, null)
    }

    override suspend fun getPosts(id: String, limit: Long, offset: Long): List<Post> {
        return client
            .request(HttpMethod.Get, "${constructFullRoute(RecursiveId<UnitModel, Unit, Unit>(Unit))}/$id/posts")
            .body()
    }

}

package me.nathanfallet.extopy.repositories.users

import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.util.reflect.*
import me.nathanfallet.extopy.client.IExtopyClient
import me.nathanfallet.extopy.models.users.FollowerInUser
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.ktorx.repositories.api.APIChildModelRemoteRepository
import me.nathanfallet.ktorx.repositories.api.IAPIModelRemoteRepository
import me.nathanfallet.usecases.models.id.RecursiveId

class FollowersInUsersRemoteRepository(
    client: IExtopyClient,
    parentRepository: IAPIModelRemoteRepository<User, String, *, *>,
) : APIChildModelRemoteRepository<FollowerInUser, String, Unit, Unit, String>(
    typeInfo<FollowerInUser>(),
    typeInfo<Unit>(),
    typeInfo<Unit>(),
    typeInfo<List<FollowerInUser>>(),
    client,
    parentRepository,
    route = "followers",
    prefix = "/api/v1"
), IFollowersInUsersRemoteRepository {

    override suspend fun list(userId: String): List<FollowerInUser> {
        return list(RecursiveId<User, String, Unit>(userId), null)
    }

    override suspend fun listFollowing(userId: String): List<FollowerInUser> {
        return client.request(
            HttpMethod.Get,
            constructFullRoute(RecursiveId<User, String, Unit>(userId)).replace("followers", "following")
        ).body(listTypeInfo)
    }

    override suspend fun create(userId: String): FollowerInUser? {
        return create(Unit, RecursiveId<User, String, Unit>(userId), null)
    }

    override suspend fun delete(userId: String, followerId: String): Boolean {
        return delete(followerId, RecursiveId<User, String, Unit>(userId), null)
    }

}

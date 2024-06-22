package com.extopy.repositories.users

import com.extopy.client.IExtopyClient
import com.extopy.models.users.FollowerInUser
import com.extopy.models.users.User
import dev.kaccelero.models.RecursiveId
import dev.kaccelero.repositories.APIChildModelRemoteRepository
import dev.kaccelero.repositories.IAPIModelRemoteRepository
import dev.kaccelero.repositories.Pagination
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.reflect.*

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

    override suspend fun list(pagination: Pagination, userId: String): List<FollowerInUser> =
        list(pagination, RecursiveId<User, String, Unit>(userId), null)

    override suspend fun listFollowing(pagination: Pagination, userId: String): List<FollowerInUser> =
        client
            .request(
                HttpMethod.Get,
                constructFullRoute(RecursiveId<User, String, Unit>(userId)).replace("followers", "following")
            ) {
                parameter("limit", pagination.limit)
                parameter("offset", pagination.offset)
            }
            .body(listTypeInfo)

    override suspend fun create(userId: String): FollowerInUser? =
        create(Unit, RecursiveId<User, String, Unit>(userId), null)

    override suspend fun delete(userId: String, followerId: String): Boolean =
        delete(followerId, RecursiveId<User, String, Unit>(userId), null)

}

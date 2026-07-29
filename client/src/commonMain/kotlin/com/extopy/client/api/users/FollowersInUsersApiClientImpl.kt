package com.extopy.client.api.users

import com.extopy.api.resources.users.FollowersInUsersApi
import com.extopy.api.responses.users.FollowerInUserResponse
import com.extopy.client.api.paginate
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import kotlin.uuid.Uuid

class FollowersInUsersApiClientImpl(
    private val client: HttpClient,
) : FollowersInUsersApiClient {

    override suspend fun list(userId: Uuid, limit: Long?, offset: Long?): List<FollowerInUserResponse> = client
        .get(FollowersInUsersApi(userId)) { paginate(limit, offset) }
        .body()

    override suspend fun listFollowing(
        userId: Uuid,
        limit: Long?,
        offset: Long?,
    ): List<FollowerInUserResponse> = client
        .get(FollowersInUsersApi.Following(FollowersInUsersApi(userId))) { paginate(limit, offset) }
        .body()

    override suspend fun create(userId: Uuid): FollowerInUserResponse = client
        .post(FollowersInUsersApi(userId))
        .body()

    override suspend fun delete(userId: Uuid, followerId: Uuid) {
        client.delete(FollowersInUsersApi.Id(followerId, FollowersInUsersApi(userId)))
    }

}

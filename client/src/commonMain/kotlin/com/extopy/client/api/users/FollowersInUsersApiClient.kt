package com.extopy.client.api.users

import com.extopy.api.responses.users.FollowerInUserResponse
import kotlin.uuid.Uuid

/**
 * The followers endpoints of the API.
 */
interface FollowersInUsersApiClient {

    /**
     * Lists the accounts following [userId].
     */
    suspend fun list(userId: Uuid, limit: Long? = null, offset: Long? = null): List<FollowerInUserResponse>

    /**
     * Lists the accounts [userId] follows.
     */
    suspend fun listFollowing(userId: Uuid, limit: Long? = null, offset: Long? = null): List<FollowerInUserResponse>

    /**
     * Follows [userId].
     */
    suspend fun create(userId: Uuid): FollowerInUserResponse

    /**
     * Removes the link between [followerId] and [userId].
     */
    suspend fun delete(userId: Uuid, followerId: Uuid)

}

package com.extopy.repositories.users

import com.extopy.models.users.FollowerInUser
import dev.kaccelero.repositories.Pagination

interface IFollowersInUsersRemoteRepository {

    suspend fun list(pagination: Pagination, userId: String): List<FollowerInUser>
    suspend fun listFollowing(pagination: Pagination, userId: String): List<FollowerInUser>
    suspend fun create(userId: String): FollowerInUser?
    suspend fun delete(userId: String, followerId: String): Boolean

}

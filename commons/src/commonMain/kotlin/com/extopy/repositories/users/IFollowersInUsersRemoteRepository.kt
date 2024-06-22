package com.extopy.repositories.users

import com.extopy.models.users.FollowerInUser
import dev.kaccelero.models.UUID
import dev.kaccelero.repositories.Pagination

interface IFollowersInUsersRemoteRepository {

    suspend fun list(pagination: Pagination, userId: UUID): List<FollowerInUser>
    suspend fun listFollowing(pagination: Pagination, userId: UUID): List<FollowerInUser>
    suspend fun create(userId: UUID): FollowerInUser?
    suspend fun delete(userId: UUID, followerId: UUID): Boolean

}

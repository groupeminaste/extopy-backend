package me.nathanfallet.extopy.repositories.users

import me.nathanfallet.extopy.models.users.FollowerInUser
import me.nathanfallet.usecases.pagination.Pagination

interface IFollowersInUsersRemoteRepository {

    suspend fun list(pagination: Pagination, userId: String): List<FollowerInUser>
    suspend fun listFollowing(pagination: Pagination, userId: String): List<FollowerInUser>
    suspend fun create(userId: String): FollowerInUser?
    suspend fun delete(userId: String, followerId: String): Boolean

}

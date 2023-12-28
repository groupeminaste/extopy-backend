package me.nathanfallet.extopy.repositories.users

import me.nathanfallet.extopy.models.users.FollowerInUser

interface IFollowersInUsersRemoteRepository {

    suspend fun list(userId: String): List<FollowerInUser>
    suspend fun listFollowing(userId: String): List<FollowerInUser>
    suspend fun create(userId: String): FollowerInUser?
    suspend fun delete(userId: String, followerId: String): Boolean

}

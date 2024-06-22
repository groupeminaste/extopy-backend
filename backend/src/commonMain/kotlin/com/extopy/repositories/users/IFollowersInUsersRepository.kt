package com.extopy.repositories.users

import com.extopy.models.users.FollowerInUser
import dev.kaccelero.models.IContext
import dev.kaccelero.repositories.IChildModelSuspendRepository
import dev.kaccelero.repositories.Pagination

interface IFollowersInUsersRepository : IChildModelSuspendRepository<FollowerInUser, String, Unit, Unit, String> {

    suspend fun listFollowing(
        pagination: Pagination,
        parentId: String,
        context: IContext? = null,
    ): List<FollowerInUser>

}

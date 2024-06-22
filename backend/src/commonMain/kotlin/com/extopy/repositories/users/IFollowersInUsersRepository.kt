package com.extopy.repositories.users

import com.extopy.models.users.FollowerInUser
import dev.kaccelero.models.IContext
import dev.kaccelero.models.UUID
import dev.kaccelero.repositories.IChildModelSuspendRepository
import dev.kaccelero.repositories.Pagination

interface IFollowersInUsersRepository : IChildModelSuspendRepository<FollowerInUser, UUID, Unit, Unit, UUID> {

    suspend fun listFollowing(
        pagination: Pagination,
        parentId: UUID,
        context: IContext? = null,
    ): List<FollowerInUser>

}

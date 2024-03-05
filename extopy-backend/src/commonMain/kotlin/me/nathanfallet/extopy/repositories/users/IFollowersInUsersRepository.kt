package me.nathanfallet.extopy.repositories.users

import me.nathanfallet.extopy.models.users.FollowerInUser
import me.nathanfallet.usecases.context.IContext
import me.nathanfallet.usecases.models.repositories.IChildModelSuspendRepository
import me.nathanfallet.usecases.pagination.Pagination

interface IFollowersInUsersRepository : IChildModelSuspendRepository<FollowerInUser, String, Unit, Unit, String> {

    suspend fun listFollowing(
        pagination: Pagination,
        parentId: String,
        context: IContext? = null,
    ): List<FollowerInUser>

}

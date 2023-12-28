package me.nathanfallet.extopy.repositories.users

import me.nathanfallet.extopy.models.users.FollowerInUser
import me.nathanfallet.usecases.context.IContext
import me.nathanfallet.usecases.models.repositories.IChildModelSuspendRepository

interface IFollowersInUsersRepository : IChildModelSuspendRepository<FollowerInUser, String, Unit, Unit, String> {

    suspend fun listFollowing(
        limit: Long,
        offset: Long,
        parentId: String,
        context: IContext? = null,
    ): List<FollowerInUser>

}

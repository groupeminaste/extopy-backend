package me.nathanfallet.extopy.controllers.users

import io.ktor.server.application.*
import me.nathanfallet.extopy.models.users.FollowerInUser
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.ktorx.controllers.IChildModelController

interface IFollowersInUsersController : IChildModelController<FollowerInUser, String, Unit, Unit, User, String> {

    suspend fun listFollowing(call: ApplicationCall, parent: User): List<FollowerInUser>

}

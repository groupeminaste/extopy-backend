package me.nathanfallet.extopy.controllers.users

import io.ktor.util.reflect.*
import me.nathanfallet.extopy.models.users.FollowerInUser
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.ktorx.routers.api.APIChildModelRouter

class FollowersInUsersRouter(
    controller: IFollowersInUsersController,
    usersRouter: UsersRouter,
) : APIChildModelRouter<FollowerInUser, String, Unit, Unit, User, String>(
    typeInfo<FollowerInUser>(),
    typeInfo<Unit>(),
    typeInfo<Unit>(),
    controller,
    usersRouter,
    IFollowersInUsersController::class,
    route = "followers",
    id = "followerId",
    prefix = "/api/v1"
)

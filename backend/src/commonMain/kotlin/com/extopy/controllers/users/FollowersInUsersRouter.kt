package com.extopy.controllers.users

import com.extopy.models.users.FollowerInUser
import com.extopy.models.users.User
import dev.kaccelero.routers.APIChildModelRouter
import io.ktor.util.reflect.*

class FollowersInUsersRouter(
    controller: IFollowersInUsersController,
    usersRouter: UsersRouter,
) : APIChildModelRouter<FollowerInUser, String, Unit, Unit, User, String>(
    typeInfo<FollowerInUser>(),
    typeInfo<Unit>(),
    typeInfo<Unit>(),
    controller,
    IFollowersInUsersController::class,
    usersRouter,
    route = "followers",
    id = "followerId",
    prefix = "/api/v1"
)

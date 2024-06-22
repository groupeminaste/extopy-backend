package com.extopy.controllers.users

import com.extopy.models.users.CreateUserPayload
import com.extopy.models.users.UpdateUserPayload
import com.extopy.models.users.User
import dev.kaccelero.routers.APIModelRouter
import io.ktor.util.reflect.*

class UsersRouter(
    controller: IUsersController,
) : APIModelRouter<User, String, CreateUserPayload, UpdateUserPayload>(
    typeInfo<User>(),
    typeInfo<CreateUserPayload>(),
    typeInfo<UpdateUserPayload>(),
    controller,
    IUsersController::class,
    prefix = "/api/v1"
)

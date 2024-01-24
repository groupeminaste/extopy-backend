package me.nathanfallet.extopy.controllers.users

import io.ktor.util.reflect.*
import me.nathanfallet.extopy.models.users.CreateUserPayload
import me.nathanfallet.extopy.models.users.UpdateUserPayload
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.ktorx.routers.api.APIModelRouter

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

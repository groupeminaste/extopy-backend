package me.nathanfallet.extopy.controllers.users

import me.nathanfallet.extopy.models.users.CreateUserPayload
import me.nathanfallet.extopy.models.users.UpdateUserPayload
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.ktorx.controllers.IModelController
import me.nathanfallet.ktorx.routers.api.APIModelRouter

class UsersRouter(
    usersController: IModelController<User, String, CreateUserPayload, UpdateUserPayload>,
) : APIModelRouter<User, String, CreateUserPayload, UpdateUserPayload>(
    User::class,
    CreateUserPayload::class,
    UpdateUserPayload::class,
    usersController,
    prefix = "/api/v1"
)

package me.nathanfallet.extopy.controllers.users

import io.ktor.util.reflect.*
import me.nathanfallet.extopy.models.users.CreateUserPayload
import me.nathanfallet.extopy.models.users.UpdateUserPayload
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.ktorx.controllers.IModelController
import me.nathanfallet.ktorx.models.api.APIMapping
import me.nathanfallet.ktorx.routers.api.APIModelRouter

class UsersRouter(
    usersController: IModelController<User, String, CreateUserPayload, UpdateUserPayload>,
) : APIModelRouter<User, String, CreateUserPayload, UpdateUserPayload>(
    typeInfo<User>(),
    typeInfo<CreateUserPayload>(),
    typeInfo<UpdateUserPayload>(),
    typeInfo<List<User>>(),
    usersController,
    mapping = APIMapping(
        listEnabled = false,
        createEnabled = false,
        deleteEnabled = false
    ),
    prefix = "/api/v1"
)

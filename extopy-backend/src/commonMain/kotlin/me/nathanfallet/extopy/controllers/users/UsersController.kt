package me.nathanfallet.extopy.controllers.users

import io.ktor.http.*
import io.ktor.server.application.*
import me.nathanfallet.extopy.models.users.CreateUserPayload
import me.nathanfallet.extopy.models.users.UpdateUserPayload
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.ktorx.controllers.IModelController
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.ktorx.usecases.users.IRequireUserForCallUseCase
import me.nathanfallet.usecases.models.get.context.IGetModelWithContextSuspendUseCase
import me.nathanfallet.usecases.models.update.IUpdateModelSuspendUseCase

class UsersController(
    private val requireUserForCallUseCase: IRequireUserForCallUseCase,
    private val getUserUseCase: IGetModelWithContextSuspendUseCase<User, String>,
    private val updateUserUseCase: IUpdateModelSuspendUseCase<User, String, UpdateUserPayload>,
) : IModelController<User, String, CreateUserPayload, UpdateUserPayload> {

    override suspend fun list(call: ApplicationCall): List<User> {
        throw ControllerException(HttpStatusCode.MethodNotAllowed, "users_list_not_allowed")
    }

    override suspend fun create(call: ApplicationCall, payload: CreateUserPayload): User {
        throw ControllerException(HttpStatusCode.MethodNotAllowed, "users_create_not_allowed")
    }

    override suspend fun delete(call: ApplicationCall, id: String) {
        throw ControllerException(HttpStatusCode.MethodNotAllowed, "users_delete_not_allowed")
    }

    override suspend fun get(call: ApplicationCall, id: String): User {
        val user = requireUserForCallUseCase(call) as User
        return getUserUseCase(id, UserContext(user.id)) ?: throw ControllerException(
            HttpStatusCode.NotFound, "users_not_found"
        )
    }

    override suspend fun update(call: ApplicationCall, id: String, payload: UpdateUserPayload): User {
        val user = (requireUserForCallUseCase(call) as User).takeIf {
            it.id == id
        } ?: throw ControllerException(
            HttpStatusCode.Forbidden, "users_update_not_allowed"
        )
        return updateUserUseCase(
            user.id, payload
        ) ?: throw ControllerException(
            HttpStatusCode.InternalServerError, "error_internal"
        )
    }

}

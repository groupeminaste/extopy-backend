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

class UsersController(
    private val requireUserForCallUseCase: IRequireUserForCallUseCase,
    private val getUserUseCase: IGetModelWithContextSuspendUseCase<User, String>,
) : IModelController<User, String, CreateUserPayload, UpdateUserPayload> {

    override suspend fun create(call: ApplicationCall, payload: CreateUserPayload): User {
        TODO("Not yet implemented")
    }

    override suspend fun delete(call: ApplicationCall, id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun get(call: ApplicationCall, id: String): User {
        val user = requireUserForCallUseCase(call) as User
        return getUserUseCase(id, UserContext(user.id)) ?: throw ControllerException(
            HttpStatusCode.NotFound, "users_not_found"
        )
    }

    override suspend fun list(call: ApplicationCall): List<User> {
        TODO("Not yet implemented")
    }

    override suspend fun update(call: ApplicationCall, id: String, payload: UpdateUserPayload): User {
        TODO("Not yet implemented")
    }

}

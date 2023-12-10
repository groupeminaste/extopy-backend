package me.nathanfallet.extopy.usecases.users

import io.ktor.http.*
import me.nathanfallet.extopy.models.users.UpdateUserPayload
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.extopy.repositories.users.IUsersRepository
import me.nathanfallet.extopy.usecases.auth.IHashPasswordUseCase
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.usecases.models.update.IUpdateModelSuspendUseCase

class UpdateUserUseCase(
    private val repository: IUsersRepository,
    private val hashPasswordUseCase: IHashPasswordUseCase,
) : IUpdateModelSuspendUseCase<User, String, UpdateUserPayload> {

    override suspend fun invoke(input1: String, input2: UpdateUserPayload): User? {
        input2.username?.let {
            repository.getForUsernameOrEmail(it, false)?.takeIf { result ->
                result.id != input1
            }?.let {
                throw ControllerException(
                    HttpStatusCode.BadRequest,
                    "auth_register_username_taken"
                )
            }
        }
        val updatedUser = input2.password?.let {
            input2.copy(password = hashPasswordUseCase(it))
        } ?: input2
        return if (repository.update(input1, updatedUser)) repository.get(input1)
        else null
    }

}

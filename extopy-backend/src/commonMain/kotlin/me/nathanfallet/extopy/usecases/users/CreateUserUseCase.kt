package me.nathanfallet.extopy.usecases.users

import io.ktor.http.*
import me.nathanfallet.extopy.models.users.CreateUserPayload
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.extopy.repositories.users.IUsersRepository
import me.nathanfallet.extopy.usecases.auth.IHashPasswordUseCase
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.usecases.models.create.ICreateModelSuspendUseCase

class CreateUserUseCase(
    private val repository: IUsersRepository,
    private val hashPasswordUseCase: IHashPasswordUseCase,
) : ICreateModelSuspendUseCase<User, CreateUserPayload> {

    override suspend fun invoke(input: CreateUserPayload): User? {
        // TODO: Update with https://github.com/nathanfallet/usecases/issues/16
        if (input.username.length > 25) throw ControllerException(
            HttpStatusCode.BadRequest,
            "auth_register_username_too_long"
        )
        if (input.displayName.length > 40) throw ControllerException(
            HttpStatusCode.BadRequest,
            "auth_register_displayName_too_long"
        )
        if (!User.usernameRegex.matches(input.username)) throw ControllerException(
            HttpStatusCode.BadRequest,
            "auth_register_username_invalid"
        )
        repository.getForUsernameOrEmail(input.username, false)?.let {
            throw ControllerException(
                HttpStatusCode.BadRequest,
                "auth_register_username_taken"
            )
        }
        return repository.create(
            input.copy(
                password = hashPasswordUseCase(input.password)
            )
        )
    }

}

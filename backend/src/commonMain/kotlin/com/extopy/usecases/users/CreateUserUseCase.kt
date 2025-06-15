package com.extopy.usecases.users

import com.extopy.models.users.CreateUserPayload
import com.extopy.models.users.User
import com.extopy.database.users.IUsersRepository
import dev.kaccelero.commons.auth.IHashPasswordUseCase
import dev.kaccelero.commons.exceptions.ControllerException
import dev.kaccelero.commons.repositories.ICreateModelSuspendUseCase
import io.ktor.http.*

class CreateUserUseCase(
    private val repository: IUsersRepository,
    private val hashPasswordUseCase: IHashPasswordUseCase,
) : ICreateModelSuspendUseCase<User, CreateUserPayload> {

    override suspend fun invoke(input: CreateUserPayload): User? {
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

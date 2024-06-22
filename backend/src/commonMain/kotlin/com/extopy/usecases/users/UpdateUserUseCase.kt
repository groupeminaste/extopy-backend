package com.extopy.usecases.users

import com.extopy.models.users.UpdateUserPayload
import com.extopy.models.users.User
import com.extopy.repositories.users.IUsersRepository
import dev.kaccelero.commons.auth.IHashPasswordUseCase
import dev.kaccelero.commons.exceptions.ControllerException
import dev.kaccelero.commons.repositories.IUpdateModelSuspendUseCase
import dev.kaccelero.models.UUID
import io.ktor.http.*

class UpdateUserUseCase(
    private val repository: IUsersRepository,
    private val hashPasswordUseCase: IHashPasswordUseCase,
) : IUpdateModelSuspendUseCase<User, UUID, UpdateUserPayload> {

    override suspend fun invoke(input1: UUID, input2: UpdateUserPayload): User? {
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

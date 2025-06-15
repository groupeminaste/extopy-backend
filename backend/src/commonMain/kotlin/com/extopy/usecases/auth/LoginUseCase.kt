package com.extopy.usecases.auth

import com.extopy.models.auth.LoginPayload
import com.extopy.models.users.User
import com.extopy.database.users.IUsersRepository
import dev.kaccelero.commons.auth.IVerifyPasswordUseCase
import dev.kaccelero.commons.auth.VerifyPasswordPayload

class LoginUseCase(
    private val repository: IUsersRepository,
    private val verifyPasswordUseCase: IVerifyPasswordUseCase,
) : ILoginUseCase {

    override suspend fun invoke(input: LoginPayload): User? =
        repository.getForUsernameOrEmail(input.username, true)?.takeIf {
            verifyPasswordUseCase(VerifyPasswordPayload(input.password, it.password ?: ""))
        }

}

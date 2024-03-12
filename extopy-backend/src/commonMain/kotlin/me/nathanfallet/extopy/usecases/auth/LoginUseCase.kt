package me.nathanfallet.extopy.usecases.auth

import me.nathanfallet.extopy.models.auth.LoginPayload
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.extopy.repositories.users.IUsersRepository

class LoginUseCase(
    private val repository: IUsersRepository,
    private val verifyPasswordUseCase: IVerifyPasswordUseCase,
) : ILoginUseCase {

    override suspend fun invoke(input: LoginPayload): User? =
        repository.getForUsernameOrEmail(input.username, true)?.takeIf {
            verifyPasswordUseCase(input.password, it.password ?: "")
        }

}

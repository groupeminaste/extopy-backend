package me.nathanfallet.extopy.usecases.auth

import me.nathanfallet.extopy.models.auth.LoginPayload
import me.nathanfallet.extopy.repositories.users.IUsersRepository
import me.nathanfallet.ktorx.usecases.auth.ILoginUseCase
import me.nathanfallet.usecases.users.IUser

class LoginUseCase(
    private val repository: IUsersRepository,
    private val verifyPasswordUseCase: IVerifyPasswordUseCase,
) : ILoginUseCase<LoginPayload> {

    override suspend fun invoke(input: LoginPayload): IUser? {
        return repository.getForUsernameOrEmail(input.username, true)?.takeIf {
            verifyPasswordUseCase(input.password, it.password ?: "")
        }
    }

}

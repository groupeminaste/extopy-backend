package com.extopy.domain.usecases.auth

import com.extopy.domain.models.auth.LoginPayload
import com.extopy.domain.models.users.User
import com.extopy.domain.repositories.UsersRepository
import com.extopy.domain.services.PasswordEncoderService

class LoginUseCaseImpl(
    private val repository: UsersRepository,
    private val passwordEncoderService: PasswordEncoderService,
) : LoginUseCase {

    override suspend fun invoke(payload: LoginPayload): User? = repository
        .getForUsernameOrEmail(payload.username, includePassword = true)
        ?.takeIf { passwordEncoderService.matches(payload.password, it.password.orEmpty()) }
        // The hash must not travel any further than the check it was read for.
        ?.copy(password = null)

}

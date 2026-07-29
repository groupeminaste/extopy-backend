package com.extopy.domain.usecases.users

import com.extopy.domain.exceptions.auth.UsernameAlreadyTakenException
import com.extopy.domain.exceptions.validation.ValidationException
import com.extopy.domain.models.users.CreateUserPayload
import com.extopy.domain.models.users.User
import com.extopy.domain.repositories.UsersRepository
import com.extopy.domain.services.PasswordEncoderService

class CreateUserUseCaseImpl(
    private val repository: UsersRepository,
    private val passwordEncoderService: PasswordEncoderService,
) : CreateUserUseCase {

    override suspend fun invoke(payload: CreateUserPayload): User? {
        validate(payload)
        if (repository.getForUsernameOrEmail(payload.username, includePassword = false) != null) {
            throw UsernameAlreadyTakenException()
        }
        return repository.create(payload.copy(password = passwordEncoderService.encode(payload.password)))
    }

    /**
     * The keys are the ones the sign-up form already reads, so the page keeps naming these the same
     * way it did when the framework validated the payload for us.
     */
    private fun validate(payload: CreateUserPayload) {
        if (!USERNAME_REGEX.matches(payload.username)) throw ValidationException("auth_username_regex")
        if (payload.username.length > User.USERNAME_MAX_LENGTH) throw ValidationException("auth_username_maxLength")
        if (payload.displayName.length > User.DISPLAY_NAME_MAX_LENGTH) {
            throw ValidationException("auth_displayName_maxLength")
        }
    }

    companion object {

        private val USERNAME_REGEX = Regex(User.USERNAME_REGEX)

    }

}

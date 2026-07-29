package com.extopy.domain.usecases.users

import com.extopy.domain.exceptions.auth.UsernameAlreadyTakenException
import com.extopy.domain.exceptions.validation.ValidationException
import com.extopy.domain.models.users.UpdateUserPayload
import com.extopy.domain.models.users.User
import com.extopy.domain.repositories.UsersRepository
import com.extopy.domain.services.PasswordEncoderService
import kotlin.uuid.Uuid

class UpdateUserUseCaseImpl(
    private val repository: UsersRepository,
    private val passwordEncoderService: PasswordEncoderService,
) : UpdateUserUseCase {

    override suspend fun invoke(id: Uuid, payload: UpdateUserPayload): User? {
        validate(payload)
        payload.username?.let { username ->
            repository.getForUsernameOrEmail(username, includePassword = false)
                ?.takeIf { it.id != id }
                ?.let { throw UsernameAlreadyTakenException() }
        }
        val toWrite = payload.password
            ?.let { payload.copy(password = passwordEncoderService.encode(it)) }
            ?: payload
        if (!repository.update(id, toWrite)) return null
        // A user only ever updates its own account, so it is also the one reading it back.
        return repository.get(id, id)
    }

    /**
     * The keys carry the `users_` prefix the message bundle already uses for this form.
     */
    private fun validate(payload: UpdateUserPayload) {
        payload.username?.let {
            if (!USERNAME_REGEX.matches(it)) throw ValidationException("users_username_regex")
            if (it.length > User.USERNAME_MAX_LENGTH) throw ValidationException("users_username_maxLength")
        }
        payload.displayName?.let {
            if (it.length > User.DISPLAY_NAME_MAX_LENGTH) throw ValidationException("users_displayName_maxLength")
        }
    }

    companion object {

        private val USERNAME_REGEX = Regex(User.USERNAME_REGEX)

    }

}

package com.extopy.domain.usecases.users

import com.extopy.domain.models.users.CreateUserPayload
import com.extopy.domain.models.users.User

/**
 * Opens an account, checking the username is free and hashing the password on the way.
 */
interface CreateUserUseCase {

    suspend operator fun invoke(payload: CreateUserPayload): User?

}

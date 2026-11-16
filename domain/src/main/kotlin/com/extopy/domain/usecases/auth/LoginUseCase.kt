package com.extopy.domain.usecases.auth

import com.extopy.domain.models.auth.LoginPayload
import com.extopy.domain.models.users.User

/**
 * Resolves the account a set of credentials stands for.
 */
interface LoginUseCase {

    /**
     * @return The account if the credentials hold up, otherwise null.
     */
    suspend operator fun invoke(payload: LoginPayload): User?

}

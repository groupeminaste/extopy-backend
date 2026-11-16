package com.extopy.domain.usecases.auth

import com.extopy.domain.models.auth.RegisterCodePayload
import com.extopy.domain.models.users.User

/**
 * Opens an account for the address a registration code was sent to.
 */
interface RegisterUseCase {

    /**
     * @param code The code emailed to the address being signed up.
     * @param payload The rest of the account, as the form filled it in.
     *
     * @return The account that was opened.
     */
    suspend operator fun invoke(code: String, payload: RegisterCodePayload): User?

}

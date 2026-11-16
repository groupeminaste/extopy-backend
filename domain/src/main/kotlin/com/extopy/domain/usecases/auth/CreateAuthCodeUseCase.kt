package com.extopy.domain.usecases.auth

import com.extopy.domain.models.auth.ClientForUser

/**
 * Draws the authorization code a client redirects with, once a user has said yes.
 */
interface CreateAuthCodeUseCase {

    /**
     * @return The code, or null if it could not be stored.
     */
    suspend operator fun invoke(clientForUser: ClientForUser): String?

}

package com.extopy.domain.usecases.auth

import com.extopy.domain.models.auth.ClientForUser

/**
 * Resolves the client and the user an authorization code stands for.
 */
interface GetAuthCodeUseCase {

    /**
     * @return The pair, or null if the code is unknown or has expired.
     */
    suspend operator fun invoke(code: String): ClientForUser?

}

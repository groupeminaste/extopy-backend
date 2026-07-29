package com.extopy.domain.usecases.auth

import com.extopy.domain.models.auth.ClientForUser

/**
 * Resolves the client and the user a refresh token stands for.
 */
interface GetClientForUserForRefreshTokenUseCase {

    /**
     * @return The pair, or null if the token does not hold up.
     */
    suspend operator fun invoke(refreshToken: String): ClientForUser?

}

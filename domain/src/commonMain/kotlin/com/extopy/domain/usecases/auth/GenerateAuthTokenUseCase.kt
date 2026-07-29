package com.extopy.domain.usecases.auth

import com.extopy.domain.models.auth.AuthToken
import com.extopy.domain.models.auth.ClientForUser

/**
 * Issues the pair of tokens a client leaves with.
 */
interface GenerateAuthTokenUseCase {

    suspend operator fun invoke(clientForUser: ClientForUser): AuthToken

}

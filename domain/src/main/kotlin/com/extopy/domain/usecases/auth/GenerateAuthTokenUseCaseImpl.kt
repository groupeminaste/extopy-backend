package com.extopy.domain.usecases.auth

import com.extopy.domain.models.auth.AuthToken
import com.extopy.domain.models.auth.ClientForUser
import com.extopy.domain.models.auth.TokenType
import com.extopy.domain.services.TokenService

class GenerateAuthTokenUseCaseImpl(
    private val tokenService: TokenService,
) : GenerateAuthTokenUseCase {

    override suspend fun invoke(clientForUser: ClientForUser) = AuthToken(
        accessToken = tokenService.generateToken(clientForUser.user.id, clientForUser.client.id, TokenType.ACCESS),
        refreshToken = tokenService.generateToken(clientForUser.user.id, clientForUser.client.id, TokenType.REFRESH),
        idToken = clientForUser.user.id,
    )

}

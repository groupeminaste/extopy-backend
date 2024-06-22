package com.extopy.usecases.auth

import com.extopy.models.auth.AuthToken
import com.extopy.models.auth.ClientForUser
import com.extopy.services.jwt.IJWTService

class GenerateAuthTokenUseCase(
    private val jwtService: IJWTService,
) : IGenerateAuthTokenUseCase {

    override suspend fun invoke(input: ClientForUser) = AuthToken(
        jwtService.generateJWT(input.user.id, input.client.id, "access"),
        jwtService.generateJWT(input.user.id, input.client.id, "refresh"),
        input.user.id
    )

}

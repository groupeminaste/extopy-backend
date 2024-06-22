package com.extopy.usecases.auth

import com.extopy.models.auth.AuthToken
import com.extopy.models.auth.ClientForUser
import com.extopy.services.jwt.IJWTService
import dev.kaccelero.models.UUID

class GenerateAuthTokenUseCase(
    private val jwtService: IJWTService,
) : IGenerateAuthTokenUseCase {

    override suspend fun invoke(input: ClientForUser) = AuthToken(
        jwtService.generateJWT(input.user.id, input.client.clientId, "access"),
        jwtService.generateJWT(input.user.id, input.client.clientId, "refresh"),
        input.user.id.let(::UUID)
    )

}

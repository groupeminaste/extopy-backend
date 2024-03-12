package me.nathanfallet.extopy.usecases.auth

import me.nathanfallet.extopy.models.auth.ClientForUser
import me.nathanfallet.extopy.services.jwt.IJWTService
import me.nathanfallet.usecases.auth.AuthToken

class GenerateAuthTokenUseCase(
    private val jwtService: IJWTService,
) : IGenerateAuthTokenUseCase {

    override suspend fun invoke(input: ClientForUser) = AuthToken(
        jwtService.generateJWT(input.user.id, input.client.clientId, "access"),
        jwtService.generateJWT(input.user.id, input.client.clientId, "refresh"),
        input.user.id
    )

}

package me.nathanfallet.extopy.usecases.auth

import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.extopy.services.jwt.IJWTService
import me.nathanfallet.ktorx.models.auth.ClientForUser
import me.nathanfallet.ktorx.usecases.auth.IGenerateAuthTokenUseCase
import me.nathanfallet.usecases.auth.AuthToken

class GenerateAuthTokenUseCase(
    private val jwtService: IJWTService,
) : IGenerateAuthTokenUseCase {

    override suspend fun invoke(input: ClientForUser): AuthToken {
        val userId = (input.user as User).id
        return AuthToken(
            jwtService.generateJWT(userId, input.client.clientId, "access"),
            jwtService.generateJWT(userId, input.client.clientId, "refresh"),
            userId
        )
    }

}

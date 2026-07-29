package com.extopy.domain.usecases.auth

import com.extopy.domain.models.auth.ClientForUser
import com.extopy.domain.services.TokenService
import com.extopy.domain.usecases.users.GetUserUseCase

class GetClientForUserForRefreshTokenUseCaseImpl(
    private val tokenService: TokenService,
    private val getUserUseCase: GetUserUseCase,
    private val getClientUseCase: GetClientUseCase,
) : GetClientForUserForRefreshTokenUseCase {

    override suspend fun invoke(refreshToken: String): ClientForUser? {
        val payload = tokenService.verifyToken(refreshToken) ?: return null
        val clientId = payload.clientId ?: return null
        val user = getUserUseCase(payload.userId, payload.userId) ?: return null
        val client = getClientUseCase(clientId) ?: return null
        return ClientForUser(client, user)
    }

}

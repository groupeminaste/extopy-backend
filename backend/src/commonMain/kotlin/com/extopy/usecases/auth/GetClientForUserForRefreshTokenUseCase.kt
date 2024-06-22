package com.extopy.usecases.auth

import com.extopy.models.application.Client
import com.extopy.models.auth.ClientForUser
import com.extopy.models.users.User
import com.extopy.models.users.UserContext
import com.extopy.services.jwt.IJWTService
import dev.kaccelero.commons.repositories.IGetModelSuspendUseCase
import dev.kaccelero.commons.repositories.IGetModelWithContextSuspendUseCase

class GetClientForUserForRefreshTokenUseCase(
    private val jwtService: IJWTService,
    private val getUserUseCase: IGetModelWithContextSuspendUseCase<User, String>,
    private val getClientUseCase: IGetModelSuspendUseCase<Client, String>,
) : IGetClientForUserForRefreshTokenUseCase {

    override suspend fun invoke(input: String): ClientForUser? = jwtService.verifyJWT(input)?.let {
        val user = getUserUseCase(it.subject, UserContext(it.subject)) ?: return@let null
        val client = it.audience.singleOrNull()?.let { clientId -> getClientUseCase(clientId) } ?: return@let null
        ClientForUser(client, user)
    }

}

package com.extopy.usecases.auth

import com.extopy.models.application.Client
import com.extopy.models.auth.ClientForUser
import com.extopy.models.users.User
import com.extopy.models.users.UserContext
import com.extopy.services.jwt.IJWTService
import dev.kaccelero.commons.repositories.IGetModelSuspendUseCase
import dev.kaccelero.commons.repositories.IGetModelWithContextSuspendUseCase
import dev.kaccelero.models.UUID

class GetClientForUserForRefreshTokenUseCase(
    private val jwtService: IJWTService,
    private val getUserUseCase: IGetModelWithContextSuspendUseCase<User, UUID>,
    private val getClientUseCase: IGetModelSuspendUseCase<Client, UUID>,
) : IGetClientForUserForRefreshTokenUseCase {

    override suspend fun invoke(input: String): ClientForUser? = jwtService.verifyJWT(input)?.let {
        val user = getUserUseCase(UUID(it.subject), UserContext(UUID(it.subject))) ?: return@let null
        val client = it.audience.singleOrNull()?.let { clientId -> getClientUseCase(UUID(clientId)) } ?: return@let null
        ClientForUser(client, user)
    }

}

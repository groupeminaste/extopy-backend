package com.extopy.domain.usecases.auth

import com.extopy.domain.models.auth.ClientForUser
import com.extopy.domain.repositories.ClientsInUsersRepository
import com.extopy.domain.usecases.users.GetUserUseCase
import kotlin.time.Clock

class GetAuthCodeUseCaseImpl(
    private val repository: ClientsInUsersRepository,
    private val getClientUseCase: GetClientUseCase,
    private val getUserUseCase: GetUserUseCase,
) : GetAuthCodeUseCase {

    override suspend fun invoke(code: String): ClientForUser? {
        val clientInUser = repository.get(code)?.takeIf { it.expiresAt > Clock.System.now() } ?: return null
        val client = getClientUseCase(clientInUser.clientId) ?: return null
        // The user reads itself, so the counters are the ones it would see of its own account.
        val user = getUserUseCase(clientInUser.userId, clientInUser.userId) ?: return null
        return ClientForUser(client, user)
    }

}

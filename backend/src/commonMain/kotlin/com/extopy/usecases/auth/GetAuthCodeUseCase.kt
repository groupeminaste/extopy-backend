package com.extopy.usecases.auth

import com.extopy.models.application.Client
import com.extopy.models.auth.ClientForUser
import com.extopy.models.users.User
import com.extopy.models.users.UserContext
import com.extopy.database.users.IClientsInUsersRepository
import dev.kaccelero.commons.repositories.IGetModelSuspendUseCase
import dev.kaccelero.commons.repositories.IGetModelWithContextSuspendUseCase
import dev.kaccelero.models.UUID
import kotlinx.datetime.Clock

class GetAuthCodeUseCase(
    private val repository: IClientsInUsersRepository,
    private val getClientUseCase: IGetModelSuspendUseCase<Client, UUID>,
    private val getUserUseCase: IGetModelWithContextSuspendUseCase<User, UUID>,
) : IGetAuthCodeUseCase {

    override suspend fun invoke(input: String): ClientForUser? {
        val clientInUser = repository.get(input)?.takeIf { it.expiration > Clock.System.now() } ?: return null
        val client = getClientUseCase(clientInUser.clientId) ?: return null
        val user = getUserUseCase(clientInUser.userId, UserContext(clientInUser.userId)) ?: return null
        return ClientForUser(client, user)
    }

}

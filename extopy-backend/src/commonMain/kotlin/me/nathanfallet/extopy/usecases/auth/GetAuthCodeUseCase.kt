package me.nathanfallet.extopy.usecases.auth

import kotlinx.datetime.Clock
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.extopy.repositories.users.IClientsInUsersRepository
import me.nathanfallet.ktorx.models.auth.ClientForUser
import me.nathanfallet.ktorx.usecases.auth.IGetAuthCodeUseCase
import me.nathanfallet.ktorx.usecases.auth.IGetClientUseCase
import me.nathanfallet.usecases.models.get.context.IGetModelWithContextSuspendUseCase

class GetAuthCodeUseCase(
    private val repository: IClientsInUsersRepository,
    private val getClientUseCase: IGetClientUseCase,
    private val getUserUseCase: IGetModelWithContextSuspendUseCase<User, String>,
) : IGetAuthCodeUseCase {

    override suspend fun invoke(input: String): ClientForUser? {
        val clientInUser = repository.get(input)?.takeIf { it.expiration > Clock.System.now() } ?: return null
        val client = getClientUseCase(clientInUser.clientId) ?: return null
        val user = getUserUseCase(clientInUser.userId, UserContext(clientInUser.userId)) ?: return null
        return ClientForUser(client, user)
    }

}

package me.nathanfallet.extopy.usecases.auth

import me.nathanfallet.extopy.models.application.Client
import me.nathanfallet.extopy.models.auth.ClientForUser
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.extopy.services.jwt.IJWTService
import me.nathanfallet.usecases.models.get.IGetModelSuspendUseCase
import me.nathanfallet.usecases.models.get.context.IGetModelWithContextSuspendUseCase

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

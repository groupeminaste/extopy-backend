package me.nathanfallet.extopy.usecases.application

import io.ktor.server.application.*
import me.nathanfallet.extopy.models.application.Client
import me.nathanfallet.extopy.usecases.auth.IGetJWTPrincipalForCallUseCase
import me.nathanfallet.usecases.auth.IClient
import me.nathanfallet.usecases.models.get.IGetModelSuspendUseCase

class GetClientForCallUseCase(
    private val getJWTPrincipalForCall: IGetJWTPrincipalForCallUseCase,
    private val getClientUseCase: IGetModelSuspendUseCase<Client, String>,
) : IGetClientForCallUseCase {

    override suspend fun invoke(input: ApplicationCall): IClient? =
        getJWTPrincipalForCall(input)?.audience?.singleOrNull()?.let { getClientUseCase(it) }

}

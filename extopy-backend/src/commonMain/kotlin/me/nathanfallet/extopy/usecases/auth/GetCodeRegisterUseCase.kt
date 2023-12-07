package me.nathanfallet.extopy.usecases.auth

import io.ktor.server.application.*
import kotlinx.datetime.Clock
import me.nathanfallet.extopy.models.auth.RegisterPayload
import me.nathanfallet.extopy.repositories.application.ICodesInEmailsRepository
import me.nathanfallet.ktorx.usecases.auth.IGetCodeRegisterUseCase

class GetCodeRegisterUseCase(
    private val repository: ICodesInEmailsRepository,
) : IGetCodeRegisterUseCase<RegisterPayload> {

    override suspend fun invoke(input1: ApplicationCall, input2: String): RegisterPayload? {
        return repository.getCodeInEmail(input2)?.takeIf {
            it.expiresAt > Clock.System.now()
        }?.let { RegisterPayload(it.email) }
    }

}

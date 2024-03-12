package me.nathanfallet.extopy.usecases.application

import kotlinx.datetime.Clock
import me.nathanfallet.extopy.models.application.CodeInEmail
import me.nathanfallet.extopy.repositories.application.ICodesInEmailsRepository

class GetCodeInEmailUseCase(
    private val repository: ICodesInEmailsRepository,
) : IGetCodeInEmailUseCase {

    override suspend fun invoke(input: String): CodeInEmail? =
        repository.getCodeInEmail(input)?.takeIf {
            it.expiresAt > Clock.System.now()
        }

}

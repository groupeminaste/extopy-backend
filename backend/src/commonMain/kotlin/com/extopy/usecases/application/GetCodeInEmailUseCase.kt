package com.extopy.usecases.application

import kotlinx.datetime.Clock
import com.extopy.models.application.CodeInEmail
import com.extopy.database.application.ICodesInEmailsRepository

class GetCodeInEmailUseCase(
    private val repository: ICodesInEmailsRepository,
) : IGetCodeInEmailUseCase {

    override suspend fun invoke(input: String): CodeInEmail? =
        repository.getCodeInEmail(input)?.takeIf {
            it.expiresAt > Clock.System.now()
        }

}

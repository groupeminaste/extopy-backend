package com.extopy.domain.usecases.application

import com.extopy.domain.models.application.CodeInEmail
import com.extopy.domain.repositories.CodesInEmailsRepository
import kotlin.time.Clock

class GetCodeInEmailUseCaseImpl(
    private val repository: CodesInEmailsRepository,
) : GetCodeInEmailUseCase {

    override suspend fun invoke(code: String): CodeInEmail? =
        repository.get(code)?.takeIf { it.expiresAt > Clock.System.now() }

}

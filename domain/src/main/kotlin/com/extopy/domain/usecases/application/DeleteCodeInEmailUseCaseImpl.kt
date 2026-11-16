package com.extopy.domain.usecases.application

import com.extopy.domain.repositories.CodesInEmailsRepository

class DeleteCodeInEmailUseCaseImpl(
    private val repository: CodesInEmailsRepository,
) : DeleteCodeInEmailUseCase {

    override suspend fun invoke(code: String) = repository.delete(code)

}

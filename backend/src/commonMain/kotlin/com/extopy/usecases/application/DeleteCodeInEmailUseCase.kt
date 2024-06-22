package com.extopy.usecases.application

import com.extopy.repositories.application.ICodesInEmailsRepository

class DeleteCodeInEmailUseCase(
    private val repository: ICodesInEmailsRepository,
) : IDeleteCodeInEmailUseCase {

    override suspend fun invoke(input: String) {
        repository.deleteCodeInEmail(input)
    }

}

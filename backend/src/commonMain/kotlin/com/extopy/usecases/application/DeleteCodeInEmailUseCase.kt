package com.extopy.usecases.application

import com.extopy.database.application.ICodesInEmailsRepository

class DeleteCodeInEmailUseCase(
    private val repository: ICodesInEmailsRepository,
) : IDeleteCodeInEmailUseCase {

    override suspend fun invoke(input: String) {
        repository.deleteCodeInEmail(input)
    }

}

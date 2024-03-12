package me.nathanfallet.extopy.usecases.application

import me.nathanfallet.extopy.repositories.application.ICodesInEmailsRepository

class DeleteCodeInEmailUseCase(
    private val repository: ICodesInEmailsRepository,
) : IDeleteCodeInEmailUseCase {

    override suspend fun invoke(input: String) {
        repository.deleteCodeInEmail(input)
    }

}

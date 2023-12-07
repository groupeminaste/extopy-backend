package me.nathanfallet.extopy.usecases.auth

import io.ktor.server.application.*
import me.nathanfallet.extopy.repositories.application.ICodesInEmailsRepository
import me.nathanfallet.ktorx.usecases.auth.IDeleteCodeRegisterUseCase

class DeleteCodeRegisterUseCase(
    private val repository: ICodesInEmailsRepository,
) : IDeleteCodeRegisterUseCase {

    override suspend fun invoke(input1: ApplicationCall, input2: String) {
        repository.deleteCodeInEmail(input2)
    }

}

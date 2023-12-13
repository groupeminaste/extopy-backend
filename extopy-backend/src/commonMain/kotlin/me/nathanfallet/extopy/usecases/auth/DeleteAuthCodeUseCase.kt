package me.nathanfallet.extopy.usecases.auth

import me.nathanfallet.extopy.repositories.users.IClientsInUsersRepository
import me.nathanfallet.ktorx.usecases.auth.IDeleteAuthCodeUseCase

class DeleteAuthCodeUseCase(
    private val repository: IClientsInUsersRepository,
) : IDeleteAuthCodeUseCase {

    override suspend fun invoke(input: String): Boolean {
        return repository.delete(input)
    }

}

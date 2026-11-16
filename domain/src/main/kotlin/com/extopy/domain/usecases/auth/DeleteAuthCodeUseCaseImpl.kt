package com.extopy.domain.usecases.auth

import com.extopy.domain.repositories.ClientsInUsersRepository

class DeleteAuthCodeUseCaseImpl(
    private val repository: ClientsInUsersRepository,
) : DeleteAuthCodeUseCase {

    override suspend fun invoke(code: String): Boolean = repository.delete(code)

}

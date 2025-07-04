package com.extopy.usecases.auth

import com.extopy.database.users.IClientsInUsersRepository

class DeleteAuthCodeUseCase(
    private val repository: IClientsInUsersRepository,
) : IDeleteAuthCodeUseCase {

    override suspend fun invoke(input: String): Boolean = repository.delete(input)

}

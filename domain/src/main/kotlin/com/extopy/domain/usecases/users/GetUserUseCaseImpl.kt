package com.extopy.domain.usecases.users

import com.extopy.domain.models.users.User
import com.extopy.domain.repositories.UsersRepository
import kotlin.uuid.Uuid

class GetUserUseCaseImpl(
    private val repository: UsersRepository,
) : GetUserUseCase {

    override suspend fun invoke(id: Uuid, viewedBy: Uuid): User? = repository.get(id, viewedBy)

}

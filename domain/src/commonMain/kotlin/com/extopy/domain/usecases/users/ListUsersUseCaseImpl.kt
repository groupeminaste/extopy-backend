package com.extopy.domain.usecases.users

import com.extopy.domain.models.application.Pagination
import com.extopy.domain.models.users.User
import com.extopy.domain.repositories.UsersRepository
import kotlin.uuid.Uuid

class ListUsersUseCaseImpl(
    private val repository: UsersRepository,
) : ListUsersUseCase {

    override suspend fun invoke(pagination: Pagination, viewedBy: Uuid): List<User> =
        repository.list(pagination, viewedBy)

}

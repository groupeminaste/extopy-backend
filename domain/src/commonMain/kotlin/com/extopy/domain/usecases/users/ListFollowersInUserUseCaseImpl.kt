package com.extopy.domain.usecases.users

import com.extopy.domain.models.application.Pagination
import com.extopy.domain.models.users.FollowerInUser
import com.extopy.domain.repositories.FollowersInUsersRepository
import kotlin.uuid.Uuid

class ListFollowersInUserUseCaseImpl(
    private val repository: FollowersInUsersRepository,
) : ListFollowersInUserUseCase {

    override suspend fun invoke(pagination: Pagination, targetId: Uuid): List<FollowerInUser> =
        repository.list(pagination, targetId)

}

package com.extopy.domain.usecases.users

import com.extopy.domain.models.application.Pagination
import com.extopy.domain.models.users.FollowerInUser
import com.extopy.domain.repositories.FollowersInUsersRepository
import kotlin.uuid.Uuid

class ListFollowingInUserUseCaseImpl(
    private val repository: FollowersInUsersRepository,
) : ListFollowingInUserUseCase {

    override suspend fun invoke(pagination: Pagination, userId: Uuid): List<FollowerInUser> =
        repository.listFollowing(pagination, userId)

}

package com.extopy.domain.usecases.users

import com.extopy.domain.repositories.FollowersInUsersRepository
import kotlin.uuid.Uuid

class DeleteFollowerInUserUseCaseImpl(
    private val repository: FollowersInUsersRepository,
) : DeleteFollowerInUserUseCase {

    override suspend fun invoke(userId: Uuid, targetId: Uuid): Boolean = repository.delete(userId, targetId)

}

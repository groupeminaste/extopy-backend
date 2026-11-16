package com.extopy.domain.usecases.users

import com.extopy.domain.exceptions.users.FollowSelfForbiddenException
import com.extopy.domain.exceptions.users.UserNotFoundException
import com.extopy.domain.models.users.FollowerInUser
import com.extopy.domain.repositories.FollowersInUsersRepository
import com.extopy.domain.repositories.UsersRepository
import kotlin.uuid.Uuid

class CreateFollowerInUserUseCaseImpl(
    private val repository: FollowersInUsersRepository,
    private val usersRepository: UsersRepository,
) : CreateFollowerInUserUseCase {

    override suspend fun invoke(targetId: Uuid, userId: Uuid): FollowerInUser? {
        if (targetId == userId) throw FollowSelfForbiddenException()
        val target = usersRepository.get(targetId, userId) ?: throw UserNotFoundException()
        // Following a personal account is a request: it only goes live once that account accepts it.
        return repository.create(userId, targetId, accepted = target.personal == false)
    }

}

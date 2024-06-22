package com.extopy.usecases.users

import com.extopy.models.users.FollowerInUser
import com.extopy.repositories.users.IFollowersInUsersRepository
import dev.kaccelero.models.UUID
import dev.kaccelero.repositories.Pagination

class ListFollowingInUserUseCase(
    private val repository: IFollowersInUsersRepository,
) : IListFollowingInUserUseCase {

    override suspend fun invoke(input1: Pagination, input2: UUID): List<FollowerInUser> =
        repository.listFollowing(input1, input2)

}

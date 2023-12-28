package me.nathanfallet.extopy.usecases.users

import me.nathanfallet.extopy.models.users.FollowerInUser
import me.nathanfallet.extopy.repositories.users.IFollowersInUsersRepository

class ListFollowingInUserUseCase(
    private val repository: IFollowersInUsersRepository,
) : IListFollowingInUserUseCase {

    override suspend fun invoke(input1: Long, input2: Long, input3: String): List<FollowerInUser> {
        return repository.listFollowing(input1, input2, input3)
    }

}

package me.nathanfallet.extopy.usecases.users

import me.nathanfallet.extopy.models.users.FollowerInUser
import me.nathanfallet.extopy.repositories.users.IFollowersInUsersRepository
import me.nathanfallet.usecases.pagination.Pagination

class ListFollowingInUserUseCase(
    private val repository: IFollowersInUsersRepository,
) : IListFollowingInUserUseCase {

    override suspend fun invoke(input1: Pagination, input2: String): List<FollowerInUser> =
        repository.listFollowing(input1, input2)

}

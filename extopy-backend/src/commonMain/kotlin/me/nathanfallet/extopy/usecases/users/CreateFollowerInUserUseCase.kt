package me.nathanfallet.extopy.usecases.users

import me.nathanfallet.extopy.models.users.FollowerInUser
import me.nathanfallet.extopy.models.users.FollowerInUserContext
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.extopy.repositories.users.IFollowersInUsersRepository
import me.nathanfallet.extopy.repositories.users.IUsersRepository
import me.nathanfallet.usecases.context.IContext
import me.nathanfallet.usecases.models.create.context.ICreateChildModelWithContextSuspendUseCase

class CreateFollowerInUserUseCase(
    private val repository: IFollowersInUsersRepository,
    private val usersRepository: IUsersRepository,
) : ICreateChildModelWithContextSuspendUseCase<FollowerInUser, Unit, String> {

    override suspend fun invoke(input1: Unit, input2: String, input3: IContext): FollowerInUser? {
        if (input3 !is UserContext) return null
        val targetUser = usersRepository.get(input2, input3) ?: return null
        return repository.create(
            input1,
            input2,
            FollowerInUserContext(
                userId = input3.userId,
                isTargetPublic = targetUser.personal == false
            )
        )
    }

}

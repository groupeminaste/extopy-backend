package com.extopy.usecases.users

import com.extopy.models.users.FollowerInUser
import com.extopy.models.users.FollowerInUserContext
import com.extopy.models.users.UserContext
import com.extopy.database.users.IFollowersInUsersRepository
import com.extopy.database.users.IUsersRepository
import dev.kaccelero.commons.exceptions.ControllerException
import dev.kaccelero.commons.repositories.ICreateChildModelWithContextSuspendUseCase
import dev.kaccelero.models.IContext
import dev.kaccelero.models.UUID
import io.ktor.http.*

class CreateFollowerInUserUseCase(
    private val repository: IFollowersInUsersRepository,
    private val usersRepository: IUsersRepository,
) : ICreateChildModelWithContextSuspendUseCase<FollowerInUser, Unit, UUID> {

    override suspend fun invoke(input1: Unit, input2: UUID, input3: IContext): FollowerInUser? {
        if (input3 !is UserContext) return null
        if (input3.userId == input2) throw ControllerException(
            HttpStatusCode.Forbidden, "followers_in_users_self_not_allowed"
        )
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

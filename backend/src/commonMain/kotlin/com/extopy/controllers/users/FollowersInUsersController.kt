package com.extopy.controllers.users

import com.extopy.models.users.FollowerInUser
import com.extopy.models.users.User
import com.extopy.models.users.UserContext
import com.extopy.usecases.users.IListFollowingInUserUseCase
import dev.kaccelero.commons.exceptions.ControllerException
import dev.kaccelero.commons.repositories.ICreateChildModelWithContextSuspendUseCase
import dev.kaccelero.commons.repositories.IDeleteChildModelSuspendUseCase
import dev.kaccelero.commons.repositories.IListSliceChildModelSuspendUseCase
import dev.kaccelero.commons.users.IRequireUserForCallUseCase
import dev.kaccelero.models.UUID
import dev.kaccelero.repositories.Pagination
import io.ktor.http.*
import io.ktor.server.application.*

class FollowersInUsersController(
    private val requireUserForCallUseCase: IRequireUserForCallUseCase,
    private val listFollowerInUserUseCase: IListSliceChildModelSuspendUseCase<FollowerInUser, UUID>,
    private val listFollowingInUserUseCase: IListFollowingInUserUseCase,
    private val createFollowerInUserUseCase: ICreateChildModelWithContextSuspendUseCase<FollowerInUser, Unit, UUID>,
    private val deleteFollowerInUserUseCase: IDeleteChildModelSuspendUseCase<FollowerInUser, UUID, UUID>,
) : IFollowersInUsersController {

    override suspend fun list(call: ApplicationCall, parent: User, limit: Long?, offset: Long?): List<FollowerInUser> {
        return listFollowerInUserUseCase(
            Pagination(
                limit ?: 25,
                offset ?: 0
            ),
            parent.id
        )
    }

    override suspend fun listFollowing(
        call: ApplicationCall,
        parent: User,
        limit: Long?,
        offset: Long?,
    ): List<FollowerInUser> {
        return listFollowingInUserUseCase(
            Pagination(
                limit ?: 25,
                offset ?: 0
            ),
            parent.id
        )
    }

    override suspend fun create(call: ApplicationCall, parent: User, payload: Unit): FollowerInUser {
        val user = requireUserForCallUseCase(call) as User
        return createFollowerInUserUseCase(
            payload,
            parent.id,
            UserContext(user.id)
        ) ?: throw ControllerException(HttpStatusCode.InternalServerError, "error_internal")
    }

    override suspend fun delete(call: ApplicationCall, parent: User, id: UUID) {
        val user = requireUserForCallUseCase(call) as User

        // I can unfollow someone, or remove someone from my followers
        if (user.id != id && user.id != parent.id) throw ControllerException(
            HttpStatusCode.Forbidden, "followers_in_users_delete_not_allowed"
        )
        if (!deleteFollowerInUserUseCase(id, parent.id)) throw ControllerException(
            HttpStatusCode.InternalServerError, "error_internal"
        )
    }

}

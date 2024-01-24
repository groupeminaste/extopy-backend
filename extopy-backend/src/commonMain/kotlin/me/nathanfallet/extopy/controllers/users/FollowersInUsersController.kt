package me.nathanfallet.extopy.controllers.users

import io.ktor.http.*
import io.ktor.server.application.*
import me.nathanfallet.extopy.models.users.FollowerInUser
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.extopy.usecases.users.IListFollowingInUserUseCase
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.ktorx.usecases.users.IRequireUserForCallUseCase
import me.nathanfallet.usecases.models.create.context.ICreateChildModelWithContextSuspendUseCase
import me.nathanfallet.usecases.models.delete.IDeleteChildModelSuspendUseCase
import me.nathanfallet.usecases.models.list.slice.IListSliceChildModelSuspendUseCase

class FollowersInUsersController(
    private val requireUserForCallUseCase: IRequireUserForCallUseCase,
    private val listFollowerInUserUseCase: IListSliceChildModelSuspendUseCase<FollowerInUser, String>,
    private val listFollowingInUserUseCase: IListFollowingInUserUseCase,
    private val createFollowerInUserUseCase: ICreateChildModelWithContextSuspendUseCase<FollowerInUser, Unit, String>,
    private val deleteFollowerInUserUseCase: IDeleteChildModelSuspendUseCase<FollowerInUser, String, String>,
) : IFollowersInUsersController {

    override suspend fun list(call: ApplicationCall, parent: User): List<FollowerInUser> {
        return listFollowerInUserUseCase(
            call.parameters["limit"]?.toLongOrNull() ?: 25,
            call.parameters["offset"]?.toLongOrNull() ?: 0,
            parent.id
        )
    }

    override suspend fun listFollowing(call: ApplicationCall, parent: User): List<FollowerInUser> {
        return listFollowingInUserUseCase(
            call.parameters["limit"]?.toLongOrNull() ?: 25,
            call.parameters["offset"]?.toLongOrNull() ?: 0,
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

    override suspend fun delete(call: ApplicationCall, parent: User, id: String) {
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

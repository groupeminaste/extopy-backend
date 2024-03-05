package me.nathanfallet.extopy.controllers.users

import io.ktor.http.*
import io.ktor.server.application.*
import me.nathanfallet.extopy.models.application.SearchOptions
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.users.UpdateUserPayload
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.extopy.usecases.users.IGetUserPostsUseCase
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.ktorx.usecases.users.IRequireUserForCallUseCase
import me.nathanfallet.usecases.models.get.context.IGetModelWithContextSuspendUseCase
import me.nathanfallet.usecases.models.list.slice.context.IListSliceModelWithContextSuspendUseCase
import me.nathanfallet.usecases.models.update.IUpdateModelSuspendUseCase
import me.nathanfallet.usecases.pagination.Pagination

class UsersController(
    private val requireUserForCallUseCase: IRequireUserForCallUseCase,
    private val listUsersUseCase: IListSliceModelWithContextSuspendUseCase<User>,
    private val getUserUseCase: IGetModelWithContextSuspendUseCase<User, String>,
    private val updateUserUseCase: IUpdateModelSuspendUseCase<User, String, UpdateUserPayload>,
    private val getUserPostsUseCase: IGetUserPostsUseCase,
) : IUsersController {

    override suspend fun list(call: ApplicationCall, limit: Long?, offset: Long?, search: String?): List<User> {
        val user = requireUserForCallUseCase(call) as User
        return listUsersUseCase(
            Pagination(
                limit ?: 25,
                offset ?: 0,
                search?.let { SearchOptions(it) }
            ),
            UserContext(user.id)
        )
    }

    override suspend fun get(call: ApplicationCall, id: String): User {
        val user = requireUserForCallUseCase(call) as User
        return getUserUseCase(id, UserContext(user.id)) ?: throw ControllerException(
            HttpStatusCode.NotFound, "users_not_found"
        )
    }

    override suspend fun update(call: ApplicationCall, id: String, payload: UpdateUserPayload): User {
        val user = (requireUserForCallUseCase(call) as User).takeIf {
            it.id == id
        } ?: throw ControllerException(
            HttpStatusCode.Forbidden, "users_update_not_allowed"
        )
        return updateUserUseCase(
            user.id, payload
        ) ?: throw ControllerException(
            HttpStatusCode.InternalServerError, "error_internal"
        )
    }

    override suspend fun listPosts(call: ApplicationCall, id: String, limit: Long?, offset: Long?): List<Post> {
        val user = requireUserForCallUseCase(call) as User
        val target = getUserUseCase(id, UserContext(user.id)) ?: throw ControllerException(
            HttpStatusCode.NotFound, "users_not_found"
        )
        return getUserPostsUseCase(
            target.id,
            Pagination(
                limit ?: 25,
                offset ?: 0
            ),
            UserContext(user.id)
        )
    }

}

package com.extopy.controllers.users

import com.extopy.models.application.SearchOptions
import com.extopy.models.posts.Post
import com.extopy.models.users.UpdateUserPayload
import com.extopy.models.users.User
import com.extopy.models.users.UserContext
import com.extopy.usecases.users.IGetUserPostsUseCase
import dev.kaccelero.commons.exceptions.ControllerException
import dev.kaccelero.commons.repositories.IGetModelWithContextSuspendUseCase
import dev.kaccelero.commons.repositories.IListSliceModelWithContextSuspendUseCase
import dev.kaccelero.commons.repositories.IUpdateModelSuspendUseCase
import dev.kaccelero.commons.users.IRequireUserForCallUseCase
import dev.kaccelero.repositories.Pagination
import io.ktor.http.*
import io.ktor.server.application.*

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

package com.extopy.presentation.routes.users

import com.extopy.api.requests.users.UpdateUserRequest
import com.extopy.api.resources.users.UsersApi
import com.extopy.domain.exceptions.users.UserNotFoundException
import com.extopy.domain.exceptions.users.UserUpdateForbiddenException
import com.extopy.domain.usecases.users.GetUserPostsUseCase
import com.extopy.domain.usecases.users.GetUserUseCase
import com.extopy.domain.usecases.users.ListUsersUseCase
import com.extopy.domain.usecases.users.UpdateUserUseCase
import com.extopy.presentation.extensions.pagination
import com.extopy.presentation.extensions.requireUser
import com.extopy.presentation.mappers.posts.toPostResponse
import com.extopy.presentation.mappers.users.toUpdateUserPayload
import com.extopy.presentation.mappers.users.toUserResponse
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Dependencies required for users routes.
 */
data class UsersRoutesDependencies(
    val listUsersUseCase: ListUsersUseCase,
    val getUserUseCase: GetUserUseCase,
    val updateUserUseCase: UpdateUserUseCase,
    val getUserPostsUseCase: GetUserPostsUseCase,
)

/**
 * Configures users routes.
 */
fun Route.usersRoutes(dependencies: UsersRoutesDependencies) = with(dependencies) {
    get<UsersApi> {
        val user = call.requireUser(getUserUseCase)
        call.respond(listUsersUseCase(call.pagination(), user.id).map { it.toUserResponse() })
    }
    get<UsersApi.Id> { resource ->
        val user = call.requireUser(getUserUseCase)
        val target = getUserUseCase(resource.userId, user.id) ?: throw UserNotFoundException()
        call.respond(target.toUserResponse())
    }
    put<UsersApi.Id> { resource ->
        val user = call.requireUser(getUserUseCase)
        // An account is the only one allowed to change itself.
        if (user.id != resource.userId) throw UserUpdateForbiddenException()
        val payload = call.receive<UpdateUserRequest>().toUpdateUserPayload()
        val updated = updateUserUseCase(user.id, payload) ?: throw UserNotFoundException()
        call.respond(updated.toUserResponse())
    }
    get<UsersApi.Id.Posts> { resource ->
        val user = call.requireUser(getUserUseCase)
        val target = getUserUseCase(resource.parent.userId, user.id) ?: throw UserNotFoundException()
        call.respond(getUserPostsUseCase(target.id, call.pagination(), user.id).map { it.toPostResponse() })
    }
}

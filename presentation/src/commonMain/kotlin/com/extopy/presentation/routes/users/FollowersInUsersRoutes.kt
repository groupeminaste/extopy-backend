package com.extopy.presentation.routes.users

import com.extopy.api.resources.users.FollowersInUsersApi
import com.extopy.domain.exceptions.users.FollowerInUserDeleteForbiddenException
import com.extopy.domain.exceptions.users.UserNotFoundException
import com.extopy.domain.models.users.User
import com.extopy.domain.usecases.users.*
import com.extopy.presentation.extensions.pagination
import com.extopy.presentation.extensions.requireUser
import com.extopy.presentation.mappers.users.toFollowerInUserResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.delete
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Dependencies required for the followers routes.
 */
data class FollowersInUsersRoutesDependencies(
    val listFollowersInUserUseCase: ListFollowersInUserUseCase,
    val listFollowingInUserUseCase: ListFollowingInUserUseCase,
    val createFollowerInUserUseCase: CreateFollowerInUserUseCase,
    val deleteFollowerInUserUseCase: DeleteFollowerInUserUseCase,
    val getUserUseCase: GetUserUseCase,
)

/**
 * Resolves the account the follow links hang off, which has to exist and be readable by the caller.
 */
private suspend fun ApplicationCall.requireTarget(
    resource: FollowersInUsersApi,
    dependencies: FollowersInUsersRoutesDependencies,
): Pair<User, User> = with(dependencies) {
    val user = requireUser(getUserUseCase)
    val target = getUserUseCase(resource.userId, user.id) ?: throw UserNotFoundException()
    user to target
}

/**
 * Configures the routes of the followers of a user.
 */
fun Route.followersInUsersRoutes(dependencies: FollowersInUsersRoutesDependencies) = with(dependencies) {
    get<FollowersInUsersApi> { resource ->
        val (_, target) = call.requireTarget(resource, dependencies)
        call.respond(listFollowersInUserUseCase(call.pagination(), target.id).map { it.toFollowerInUserResponse() })
    }
    get<FollowersInUsersApi.Following> { resource ->
        val (_, target) = call.requireTarget(resource.parent, dependencies)
        call.respond(listFollowingInUserUseCase(call.pagination(), target.id).map { it.toFollowerInUserResponse() })
    }
    post<FollowersInUsersApi> { resource ->
        val (user, target) = call.requireTarget(resource, dependencies)
        val follower = createFollowerInUserUseCase(target.id, user.id) ?: throw UserNotFoundException()
        call.respond(HttpStatusCode.Created, follower.toFollowerInUserResponse())
    }
    delete<FollowersInUsersApi.Id> { resource ->
        val (user, target) = call.requireTarget(resource.parent, dependencies)
        // Either end of a link may cut it: unfollowing an account, or dropping one of your followers.
        if (user.id != resource.followerId && user.id != target.id) {
            throw FollowerInUserDeleteForbiddenException()
        }
        deleteFollowerInUserUseCase(resource.followerId, target.id)
        call.respond(HttpStatusCode.NoContent)
    }
}

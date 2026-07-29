package com.extopy.presentation.routes.posts

import com.extopy.api.resources.posts.LikesInPostsApi
import com.extopy.domain.exceptions.posts.LikeInPostDeleteForbiddenException
import com.extopy.domain.exceptions.posts.PostNotFoundException
import com.extopy.domain.models.posts.Post
import com.extopy.domain.models.users.User
import com.extopy.domain.usecases.posts.CreateLikeInPostUseCase
import com.extopy.domain.usecases.posts.DeleteLikeInPostUseCase
import com.extopy.domain.usecases.posts.GetPostUseCase
import com.extopy.domain.usecases.posts.ListLikesInPostUseCase
import com.extopy.domain.usecases.users.GetUserUseCase
import com.extopy.presentation.extensions.pagination
import com.extopy.presentation.extensions.requireUser
import com.extopy.presentation.mappers.posts.toLikeInPostResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.delete
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Dependencies required for the likes routes.
 */
data class LikesInPostsRoutesDependencies(
    val listLikesInPostUseCase: ListLikesInPostUseCase,
    val createLikeInPostUseCase: CreateLikeInPostUseCase,
    val deleteLikeInPostUseCase: DeleteLikeInPostUseCase,
    val getPostUseCase: GetPostUseCase,
    val getUserUseCase: GetUserUseCase,
)

/**
 * Resolves the post the likes hang off, which has to exist and be readable by the caller.
 */
private suspend fun ApplicationCall.requirePost(
    resource: LikesInPostsApi,
    dependencies: LikesInPostsRoutesDependencies,
): Pair<User, Post> = with(dependencies) {
    val user = requireUser(getUserUseCase)
    val post = getPostUseCase(resource.postId, user.id) ?: throw PostNotFoundException()
    user to post
}

/**
 * Configures the routes of the likes of a post.
 */
fun Route.likesInPostsRoutes(dependencies: LikesInPostsRoutesDependencies) = with(dependencies) {
    get<LikesInPostsApi> { resource ->
        val (_, post) = call.requirePost(resource, dependencies)
        call.respond(listLikesInPostUseCase(call.pagination(), post.id).map { it.toLikeInPostResponse() })
    }
    post<LikesInPostsApi> { resource ->
        val (user, post) = call.requirePost(resource, dependencies)
        val like = createLikeInPostUseCase(post.id, user.id) ?: throw PostNotFoundException()
        call.respond(HttpStatusCode.Created, like.toLikeInPostResponse())
    }
    delete<LikesInPostsApi.Id> { resource ->
        val (user, post) = call.requirePost(resource.parent, dependencies)
        // A like belongs to whoever left it, and only they may take it back.
        if (user.id != resource.userId) throw LikeInPostDeleteForbiddenException()
        deleteLikeInPostUseCase(post.id, resource.userId)
        call.respond(HttpStatusCode.NoContent)
    }
}

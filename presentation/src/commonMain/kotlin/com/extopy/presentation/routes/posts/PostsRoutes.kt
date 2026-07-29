package com.extopy.presentation.routes.posts

import com.extopy.api.requests.posts.PostRequest
import com.extopy.api.resources.posts.PostsApi
import com.extopy.domain.exceptions.posts.PostDeleteForbiddenException
import com.extopy.domain.exceptions.posts.PostNotFoundException
import com.extopy.domain.exceptions.posts.PostUpdateForbiddenException
import com.extopy.domain.usecases.posts.*
import com.extopy.domain.usecases.users.GetUserUseCase
import com.extopy.presentation.extensions.pagination
import com.extopy.presentation.extensions.requireUser
import com.extopy.presentation.mappers.posts.toPostPayload
import com.extopy.presentation.mappers.posts.toPostResponse
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.delete
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Dependencies required for posts routes.
 */
data class PostsRoutesDependencies(
    val listPostsUseCase: ListPostsUseCase,
    val getPostUseCase: GetPostUseCase,
    val createPostUseCase: CreatePostUseCase,
    val updatePostUseCase: UpdatePostUseCase,
    val deletePostUseCase: DeletePostUseCase,
    val getPostRepliesUseCase: GetPostRepliesUseCase,
    val getUserUseCase: GetUserUseCase,
)

/**
 * Configures posts routes.
 */
fun Route.postsRoutes(dependencies: PostsRoutesDependencies) = with(dependencies) {
    get<PostsApi> {
        val user = call.requireUser(getUserUseCase)
        call.respond(listPostsUseCase(call.pagination(), user.id).map { it.toPostResponse() })
    }
    get<PostsApi.Id> { resource ->
        val user = call.requireUser(getUserUseCase)
        val post = getPostUseCase(resource.postId, user.id) ?: throw PostNotFoundException()
        call.respond(post.toPostResponse())
    }
    post<PostsApi> {
        val user = call.requireUser(getUserUseCase)
        val payload = call.receive<PostRequest>().toPostPayload()
        val post = createPostUseCase(payload, user.id) ?: throw PostNotFoundException()
        call.respond(HttpStatusCode.Created, post.toPostResponse())
    }
    put<PostsApi.Id> { resource ->
        val user = call.requireUser(getUserUseCase)
        val post = getPostUseCase(resource.postId, user.id) ?: throw PostNotFoundException()
        if (post.userId != user.id) throw PostUpdateForbiddenException()
        val payload = call.receive<PostRequest>().toPostPayload()
        val updated = updatePostUseCase(post.id, payload, user.id) ?: throw PostNotFoundException()
        call.respond(updated.toPostResponse())
    }
    delete<PostsApi.Id> { resource ->
        val user = call.requireUser(getUserUseCase)
        val post = getPostUseCase(resource.postId, user.id) ?: throw PostNotFoundException()
        if (post.userId != user.id) throw PostDeleteForbiddenException()
        deletePostUseCase(post.id)
        call.respond(HttpStatusCode.NoContent)
    }
    get<PostsApi.Id.Replies> { resource ->
        val user = call.requireUser(getUserUseCase)
        val post = getPostUseCase(resource.parent.postId, user.id) ?: throw PostNotFoundException()
        call.respond(getPostRepliesUseCase(post.id, call.pagination(), user.id).map { it.toPostResponse() })
    }
}

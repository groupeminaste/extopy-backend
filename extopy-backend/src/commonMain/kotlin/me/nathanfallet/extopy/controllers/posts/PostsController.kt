package me.nathanfallet.extopy.controllers.posts

import io.ktor.http.*
import io.ktor.server.application.*
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.posts.PostPayload
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.extopy.usecases.posts.IGetPostRepliesUseCase
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.ktorx.usecases.users.IRequireUserForCallUseCase
import me.nathanfallet.usecases.models.create.context.ICreateModelWithContextSuspendUseCase
import me.nathanfallet.usecases.models.delete.IDeleteModelSuspendUseCase
import me.nathanfallet.usecases.models.get.context.IGetModelWithContextSuspendUseCase
import me.nathanfallet.usecases.models.update.IUpdateModelSuspendUseCase

class PostsController(
    private val requireUserForCallUseCase: IRequireUserForCallUseCase,
    private val createPostUseCase: ICreateModelWithContextSuspendUseCase<Post, PostPayload>,
    private val getPostUseCase: IGetModelWithContextSuspendUseCase<Post, String>,
    private val updatePostUseCase: IUpdateModelSuspendUseCase<Post, String, PostPayload>,
    private val deletePostUseCase: IDeleteModelSuspendUseCase<Post, String>,
    private val getPostRepliesUseCase: IGetPostRepliesUseCase,
) : IPostsController {

    override suspend fun create(call: ApplicationCall, payload: PostPayload): Post {
        val user = requireUserForCallUseCase(call) as User
        return createPostUseCase(payload, UserContext(user.id)) ?: throw ControllerException(
            HttpStatusCode.InternalServerError, "error_internal"
        )
    }

    override suspend fun get(call: ApplicationCall, id: String): Post {
        val user = requireUserForCallUseCase(call) as User
        return getPostUseCase(id, UserContext(user.id)) ?: throw ControllerException(
            HttpStatusCode.NotFound, "posts_not_found"
        )
    }

    override suspend fun update(call: ApplicationCall, id: String, payload: PostPayload): Post {
        val user = requireUserForCallUseCase(call) as User
        val post = getPostUseCase(id, UserContext(user.id)) ?: throw ControllerException(
            HttpStatusCode.NotFound, "posts_not_found"
        )
        if (post.userId != user.id) throw ControllerException(
            HttpStatusCode.Forbidden, "posts_update_not_allowed"
        )
        return updatePostUseCase(
            id, payload
        ) ?: throw ControllerException(
            HttpStatusCode.InternalServerError, "error_internal"
        )
    }

    override suspend fun delete(call: ApplicationCall, id: String) {
        val user = requireUserForCallUseCase(call) as User
        val post = getPostUseCase(id, UserContext(user.id)) ?: throw ControllerException(
            HttpStatusCode.NotFound, "posts_not_found"
        )
        if (post.userId != user.id) throw ControllerException(
            HttpStatusCode.Forbidden, "posts_delete_not_allowed"
        )
        if (!deletePostUseCase(id)) throw ControllerException(
            HttpStatusCode.InternalServerError, "error_internal"
        )
    }

    override suspend fun listReplies(call: ApplicationCall, id: String): List<Post> {
        val user = requireUserForCallUseCase(call) as User
        return getPostRepliesUseCase(
            id,
            call.parameters["limit"]?.toLongOrNull() ?: 25,
            call.parameters["offset"]?.toLongOrNull() ?: 0,
            UserContext(user.id)
        )
    }

}

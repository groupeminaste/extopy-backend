package me.nathanfallet.extopy.controllers.posts

import io.ktor.http.*
import io.ktor.server.application.*
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.posts.PostPayload
import me.nathanfallet.ktorx.controllers.IModelController
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.ktorx.usecases.users.IRequireUserForCallUseCase
import me.nathanfallet.usecases.models.create.ICreateModelSuspendUseCase

class PostsController(
    private val requireUserForCallUseCase: IRequireUserForCallUseCase,
    private val createPostUseCase: ICreateModelSuspendUseCase<Post, PostPayload>,
) : IModelController<Post, String, PostPayload, PostPayload> {

    override suspend fun create(call: ApplicationCall, payload: PostPayload): Post {
        val user = requireUserForCallUseCase(call)
        return createPostUseCase(payload) ?: throw ControllerException(
            HttpStatusCode.InternalServerError, "error_internal"
        )
    }

    override suspend fun delete(call: ApplicationCall, id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun get(call: ApplicationCall, id: String): Post {
        TODO("Not yet implemented")
    }

    override suspend fun list(call: ApplicationCall): List<Post> {
        TODO("Not yet implemented")
    }

    override suspend fun update(call: ApplicationCall, id: String, payload: PostPayload): Post {
        TODO("Not yet implemented")
    }

}

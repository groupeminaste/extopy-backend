package me.nathanfallet.extopy.controllers.posts

import io.ktor.http.*
import io.ktor.server.application.*
import me.nathanfallet.extopy.models.posts.LikeInPost
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.ktorx.controllers.IChildModelController
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.ktorx.usecases.users.IRequireUserForCallUseCase
import me.nathanfallet.usecases.models.create.context.ICreateChildModelWithContextSuspendUseCase
import me.nathanfallet.usecases.models.delete.IDeleteChildModelSuspendUseCase
import me.nathanfallet.usecases.models.list.slice.IListSliceChildModelSuspendUseCase

class LikesInPostsController(
    private val requireUserForCallUseCase: IRequireUserForCallUseCase,
    private val listLikeInPostUseCase: IListSliceChildModelSuspendUseCase<LikeInPost, String>,
    private val createLikeInPostUseCase: ICreateChildModelWithContextSuspendUseCase<LikeInPost, Unit, String>,
    private val deleteLikeInPostUseCase: IDeleteChildModelSuspendUseCase<LikeInPost, String, String>,
) : IChildModelController<LikeInPost, String, Unit, Unit, Post, String> {

    override suspend fun list(call: ApplicationCall, parent: Post): List<LikeInPost> {
        return listLikeInPostUseCase(
            call.parameters["limit"]?.toLongOrNull() ?: 25,
            call.parameters["offset"]?.toLongOrNull() ?: 0,
            parent.id
        )
    }

    override suspend fun create(call: ApplicationCall, parent: Post, payload: Unit): LikeInPost {
        val user = requireUserForCallUseCase(call) as User
        return createLikeInPostUseCase(
            payload,
            parent.id,
            UserContext(user.id)
        ) ?: throw ControllerException(HttpStatusCode.InternalServerError, "error_internal")
    }

    override suspend fun delete(call: ApplicationCall, parent: Post, id: String) {
        val user = requireUserForCallUseCase(call) as User
        if (user.id != id) throw ControllerException(
            HttpStatusCode.Forbidden, "likes_in_posts_delete_not_allowed"
        )
        if (!deleteLikeInPostUseCase(id, parent.id)) throw ControllerException(
            HttpStatusCode.InternalServerError, "error_internal"
        )
    }

    override suspend fun get(call: ApplicationCall, parent: Post, id: String): LikeInPost {
        throw ControllerException(HttpStatusCode.MethodNotAllowed, "likes_in_posts_get_not_allowed")
    }

    override suspend fun update(call: ApplicationCall, parent: Post, id: String, payload: Unit): LikeInPost {
        throw ControllerException(HttpStatusCode.MethodNotAllowed, "likes_in_posts_update_not_allowed")
    }

}

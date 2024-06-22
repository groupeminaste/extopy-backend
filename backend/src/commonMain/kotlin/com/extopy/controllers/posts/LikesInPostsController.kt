package com.extopy.controllers.posts

import com.extopy.models.posts.LikeInPost
import com.extopy.models.posts.Post
import com.extopy.models.users.User
import com.extopy.models.users.UserContext
import dev.kaccelero.commons.exceptions.ControllerException
import dev.kaccelero.commons.repositories.ICreateChildModelWithContextSuspendUseCase
import dev.kaccelero.commons.repositories.IDeleteChildModelSuspendUseCase
import dev.kaccelero.commons.repositories.IListSliceChildModelSuspendUseCase
import dev.kaccelero.commons.users.IRequireUserForCallUseCase
import dev.kaccelero.repositories.Pagination
import io.ktor.http.*
import io.ktor.server.application.*

class LikesInPostsController(
    private val requireUserForCallUseCase: IRequireUserForCallUseCase,
    private val listLikeInPostUseCase: IListSliceChildModelSuspendUseCase<LikeInPost, String>,
    private val createLikeInPostUseCase: ICreateChildModelWithContextSuspendUseCase<LikeInPost, Unit, String>,
    private val deleteLikeInPostUseCase: IDeleteChildModelSuspendUseCase<LikeInPost, String, String>,
) : ILikesInPostsController {

    override suspend fun list(call: ApplicationCall, parent: Post, limit: Long?, offset: Long?): List<LikeInPost> {
        return listLikeInPostUseCase(
            Pagination(
                limit ?: 25,
                offset ?: 0
            ),
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

}

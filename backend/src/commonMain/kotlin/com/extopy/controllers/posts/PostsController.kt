package com.extopy.controllers.posts

import com.extopy.models.application.SearchOptions
import com.extopy.models.posts.Post
import com.extopy.models.posts.PostPayload
import com.extopy.models.users.User
import com.extopy.models.users.UserContext
import com.extopy.usecases.posts.IGetPostRepliesUseCase
import dev.kaccelero.commons.exceptions.ControllerException
import dev.kaccelero.commons.repositories.*
import dev.kaccelero.commons.users.IRequireUserForCallUseCase
import dev.kaccelero.repositories.Pagination
import io.ktor.http.*
import io.ktor.server.application.*

class PostsController(
    private val requireUserForCallUseCase: IRequireUserForCallUseCase,
    private val createPostUseCase: ICreateModelWithContextSuspendUseCase<Post, PostPayload>,
    private val listPostsUseCase: IListSliceModelWithContextSuspendUseCase<Post>,
    private val getPostUseCase: IGetModelWithContextSuspendUseCase<Post, String>,
    private val updatePostUseCase: IUpdateModelSuspendUseCase<Post, String, PostPayload>,
    private val deletePostUseCase: IDeleteModelSuspendUseCase<Post, String>,
    private val getPostRepliesUseCase: IGetPostRepliesUseCase,
) : IPostsController {

    override suspend fun list(call: ApplicationCall, limit: Long?, offset: Long?, search: String?): List<Post> {
        val user = requireUserForCallUseCase(call) as User
        return listPostsUseCase(
            Pagination(
                limit ?: 25,
                offset ?: 0,
                search?.let { SearchOptions(it) }
            ),
            UserContext(user.id)
        )
    }

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

    override suspend fun listReplies(call: ApplicationCall, id: String, limit: Long?, offset: Long?): List<Post> {
        val user = requireUserForCallUseCase(call) as User
        val post = getPostUseCase(id, UserContext(user.id)) ?: throw ControllerException(
            HttpStatusCode.NotFound, "posts_not_found"
        )
        return getPostRepliesUseCase(
            post.id,
            Pagination(
                limit ?: 25,
                offset ?: 0
            ),
            UserContext(user.id)
        )
    }

}

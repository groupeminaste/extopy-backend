package me.nathanfallet.extopy.controllers.timelines

import io.ktor.http.*
import io.ktor.server.application.*
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.timelines.Timeline
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.extopy.usecases.timelines.IGetTimelineByIdUseCase
import me.nathanfallet.extopy.usecases.timelines.IGetTimelinePostsUseCase
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.ktorx.usecases.users.IRequireUserForCallUseCase

class TimelinesController(
    private val requireUserForCallUseCase: IRequireUserForCallUseCase,
    private val getTimelineUseCase: IGetTimelineByIdUseCase,
    private val getTimelinePostsUseCase: IGetTimelinePostsUseCase,
) : ITimelinesController {

    override suspend fun list(call: ApplicationCall): List<Timeline> {
        throw ControllerException(HttpStatusCode.MethodNotAllowed, "timelines_list_not_allowed")
    }

    override suspend fun get(call: ApplicationCall, id: String): Timeline {
        val user = requireUserForCallUseCase(call) as User
        return getTimelineUseCase(id, UserContext(user.id)) ?: throw ControllerException(
            HttpStatusCode.NotFound, "timelines_not_found"
        )
    }

    override suspend fun getPosts(call: ApplicationCall, id: String): List<Post> {
        val user = requireUserForCallUseCase(call) as User
        return getTimelinePostsUseCase(
            id,
            call.parameters["limit"]?.toLongOrNull() ?: 25,
            call.parameters["offset"]?.toLongOrNull() ?: 0,
            UserContext(user.id)
        )
    }

    override suspend fun create(call: ApplicationCall, payload: Unit): Timeline {
        throw ControllerException(HttpStatusCode.MethodNotAllowed, "timelines_create_not_allowed")
    }

    override suspend fun update(call: ApplicationCall, id: String, payload: Unit): Timeline {
        throw ControllerException(HttpStatusCode.MethodNotAllowed, "timelines_update_not_allowed")
    }

    override suspend fun delete(call: ApplicationCall, id: String) {
        throw ControllerException(HttpStatusCode.MethodNotAllowed, "timelines_delete_not_allowed")
    }

}

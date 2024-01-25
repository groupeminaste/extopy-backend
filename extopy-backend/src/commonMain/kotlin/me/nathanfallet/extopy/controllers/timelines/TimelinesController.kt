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

    override suspend fun get(call: ApplicationCall, id: String): Timeline {
        val user = requireUserForCallUseCase(call) as User
        return getTimelineUseCase(id, UserContext(user.id)) ?: throw ControllerException(
            HttpStatusCode.NotFound, "timelines_not_found"
        )
    }

    override suspend fun listPosts(call: ApplicationCall, id: String): List<Post> {
        val user = requireUserForCallUseCase(call) as User
        val timeline = getTimelineUseCase(id, UserContext(user.id)) ?: throw ControllerException(
            HttpStatusCode.NotFound, "timelines_not_found"
        )
        return getTimelinePostsUseCase(
            timeline.id,
            call.parameters["limit"]?.toLongOrNull() ?: 25,
            call.parameters["offset"]?.toLongOrNull() ?: 0,
            UserContext(user.id)
        )
    }

}

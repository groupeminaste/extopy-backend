package com.extopy.controllers.timelines

import com.extopy.models.posts.Post
import com.extopy.models.timelines.Timeline
import com.extopy.models.users.User
import com.extopy.models.users.UserContext
import com.extopy.usecases.timelines.IGetTimelineByIdUseCase
import com.extopy.usecases.timelines.IGetTimelinePostsUseCase
import dev.kaccelero.commons.exceptions.ControllerException
import dev.kaccelero.commons.users.IRequireUserForCallUseCase
import dev.kaccelero.repositories.Pagination
import io.ktor.http.*
import io.ktor.server.application.*

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

    override suspend fun listPosts(call: ApplicationCall, id: String, limit: Long?, offset: Long?): List<Post> {
        val user = requireUserForCallUseCase(call) as User
        val timeline = getTimelineUseCase(id, UserContext(user.id)) ?: throw ControllerException(
            HttpStatusCode.NotFound, "timelines_not_found"
        )
        return getTimelinePostsUseCase(
            timeline.id,
            Pagination(
                limit ?: 25,
                offset ?: 0
            ),
            UserContext(user.id)
        )
    }

}

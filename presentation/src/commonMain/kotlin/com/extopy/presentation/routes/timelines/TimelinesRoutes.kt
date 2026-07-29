package com.extopy.presentation.routes.timelines

import com.extopy.api.resources.timelines.TimelinesApi
import com.extopy.domain.exceptions.timelines.TimelineNotFoundException
import com.extopy.domain.usecases.timelines.GetTimelinePostsUseCase
import com.extopy.domain.usecases.timelines.GetTimelineUseCase
import com.extopy.domain.usecases.users.GetUserUseCase
import com.extopy.presentation.extensions.pagination
import com.extopy.presentation.extensions.requireUser
import com.extopy.presentation.mappers.posts.toPostResponse
import com.extopy.presentation.mappers.timelines.toTimelineResponse
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Dependencies required for timelines routes.
 */
data class TimelinesRoutesDependencies(
    val getTimelineUseCase: GetTimelineUseCase,
    val getTimelinePostsUseCase: GetTimelinePostsUseCase,
    val getUserUseCase: GetUserUseCase,
)

/**
 * Configures timelines routes.
 */
fun Route.timelinesRoutes(dependencies: TimelinesRoutesDependencies) = with(dependencies) {
    get<TimelinesApi.Id> { resource ->
        call.requireUser(getUserUseCase)
        val timeline = getTimelineUseCase(resource.timelineId) ?: throw TimelineNotFoundException()
        call.respond(timeline.toTimelineResponse())
    }
    get<TimelinesApi.Id.Posts> { resource ->
        val user = call.requireUser(getUserUseCase)
        val timeline = getTimelineUseCase(resource.parent.timelineId) ?: throw TimelineNotFoundException()
        call.respond(getTimelinePostsUseCase(timeline.id, call.pagination(), user.id).map { it.toPostResponse() })
    }
}

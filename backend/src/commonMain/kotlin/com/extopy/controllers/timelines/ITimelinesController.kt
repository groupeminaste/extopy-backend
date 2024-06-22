package com.extopy.controllers.timelines

import com.extopy.models.posts.Post
import com.extopy.models.timelines.Timeline
import dev.kaccelero.annotations.*
import dev.kaccelero.controllers.IModelController
import dev.kaccelero.models.UUID
import io.ktor.server.application.*

interface ITimelinesController : IModelController<Timeline, UUID, Unit, Unit> {

    @APIMapping
    @GetModelPath
    @DocumentedError(401, "auth_invalid_credentials")
    @DocumentedError(404, "timelines_not_found")
    suspend fun get(call: ApplicationCall, @Id id: UUID): Timeline

    @APIMapping("listTimelinePost", "Get timeline posts by id")
    @Path("GET", "/{timelineId}/posts")
    @DocumentedError(401, "auth_invalid_credentials")
    @DocumentedError(404, "timelines_not_found")
    suspend fun listPosts(
        call: ApplicationCall,
        @Id id: UUID,
        @QueryParameter limit: Long?,
        @QueryParameter offset: Long?,
    ): List<Post>

}

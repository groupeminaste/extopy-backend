package me.nathanfallet.extopy.controllers.timelines

import io.ktor.server.application.*
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.timelines.Timeline
import me.nathanfallet.ktorx.controllers.IModelController
import me.nathanfallet.ktorx.models.annotations.*

interface ITimelinesController : IModelController<Timeline, String, Unit, Unit> {

    @APIMapping
    @GetModelPath
    @DocumentedError(401, "auth_invalid_credentials")
    @DocumentedError(404, "timelines_not_found")
    suspend fun get(call: ApplicationCall, @Id id: String): Timeline

    @APIMapping("listTimelinePost", "Get timeline posts by id")
    @Path("GET", "/{timelineId}/posts")
    @DocumentedError(401, "auth_invalid_credentials")
    @DocumentedError(404, "timelines_not_found")
    suspend fun listPosts(call: ApplicationCall, @Id id: String): List<Post>

}

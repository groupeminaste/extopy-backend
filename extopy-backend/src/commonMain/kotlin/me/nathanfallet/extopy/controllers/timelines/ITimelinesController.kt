package me.nathanfallet.extopy.controllers.timelines

import io.ktor.server.application.*
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.timelines.Timeline
import me.nathanfallet.ktorx.controllers.IModelController
import me.nathanfallet.ktorx.models.annotations.APIMapping
import me.nathanfallet.ktorx.models.annotations.GetPath
import me.nathanfallet.ktorx.models.annotations.Path

interface ITimelinesController : IModelController<Timeline, String, Unit, Unit> {

    @APIMapping
    @GetPath
    suspend fun get(call: ApplicationCall, id: String): Timeline

    @APIMapping("listTimelinePost", "Get timeline posts by id")
    @Path("GET", "/{timelineId}/posts")
    suspend fun listPosts(call: ApplicationCall, id: String): List<Post>

}

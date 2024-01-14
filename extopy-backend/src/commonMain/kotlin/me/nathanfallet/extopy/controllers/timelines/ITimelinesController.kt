package me.nathanfallet.extopy.controllers.timelines

import io.ktor.server.application.*
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.timelines.Timeline
import me.nathanfallet.ktorx.controllers.IModelController

interface ITimelinesController : IModelController<Timeline, String, Unit, Unit> {

    suspend fun getPosts(call: ApplicationCall, id: String): List<Post>

}

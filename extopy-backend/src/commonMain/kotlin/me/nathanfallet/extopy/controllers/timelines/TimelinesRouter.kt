package me.nathanfallet.extopy.controllers.timelines

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*
import io.swagger.v3.oas.models.OpenAPI
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.timelines.Timeline
import me.nathanfallet.ktorx.extensions.arraySchema
import me.nathanfallet.ktorx.extensions.get
import me.nathanfallet.ktorx.extensions.mediaType
import me.nathanfallet.ktorx.extensions.response
import me.nathanfallet.ktorx.models.api.APIMapping
import me.nathanfallet.ktorx.routers.api.APIModelRouter

class TimelinesRouter(
    override val controller: ITimelinesController,
) : APIModelRouter<Timeline, String, Unit, Unit>(
    typeInfo<Timeline>(),
    typeInfo<Unit>(),
    typeInfo<Unit>(),
    typeInfo<List<Timeline>>(),
    controller,
    mapping = APIMapping(
        listEnabled = false,
        createEnabled = false,
        updateEnabled = false,
        deleteEnabled = false
    ),
    prefix = "/api/v1"
) {

    override fun createRoutes(root: Route, openAPI: OpenAPI?) {
        super.createRoutes(root, openAPI)
        createAPIGetPostsRoute(root, openAPI)
    }

    fun createAPIGetPostsRoute(root: Route, openAPI: OpenAPI?) {
        root.get("$fullRoute/{$id}/posts") {
            try {
                call.respond(controller.getPosts(call, get(call).id))
            } catch (exception: Exception) {
                handleExceptionAPI(exception, call)
            }
        }
        openAPI?.get("$fullRoute/{$id}/posts") {
            operationId("getTimelinePostsById")
            addTagsItem("Timeline")
            description("Get timeline posts by id")
            parameters(getOpenAPIParameters())
            response("200") {
                description("Posts for timeline")
                mediaType("application/json") {
                    arraySchema(Post::class)
                }
            }
        }
    }

}

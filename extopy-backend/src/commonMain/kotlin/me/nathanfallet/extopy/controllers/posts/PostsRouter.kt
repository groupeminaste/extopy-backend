package me.nathanfallet.extopy.controllers.posts

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*
import io.swagger.v3.oas.models.OpenAPI
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.posts.PostPayload
import me.nathanfallet.ktorx.extensions.arraySchema
import me.nathanfallet.ktorx.extensions.get
import me.nathanfallet.ktorx.extensions.mediaType
import me.nathanfallet.ktorx.extensions.response
import me.nathanfallet.ktorx.models.api.APIMapping
import me.nathanfallet.ktorx.routers.api.APIModelRouter

class PostsRouter(
    override val controller: IPostsController,
) : APIModelRouter<Post, String, PostPayload, PostPayload>(
    typeInfo<Post>(),
    typeInfo<PostPayload>(),
    typeInfo<PostPayload>(),
    typeInfo<List<Post>>(),
    controller,
    mapping = APIMapping(
        listEnabled = false
    ),
    prefix = "/api/v1"
) {

    override fun createRoutes(root: Route, openAPI: OpenAPI?) {
        super.createRoutes(root, openAPI)
        createAPIGetRepliesRoute(root, openAPI)
    }

    fun createAPIGetRepliesRoute(root: Route, openAPI: OpenAPI?) {
        root.get("$fullRoute/{$id}/replies") {
            try {
                call.respond(controller.getReplies(call, get(call).id))
            } catch (exception: Exception) {
                handleExceptionAPI(exception, call)
            }
        }
        openAPI?.get("$fullRoute/{$id}/replies") {
            operationId("getPostRepliesById")
            addTagsItem("Post")
            description("Get post replies by id")
            parameters(getOpenAPIParameters())
            response("200") {
                description("Replies to a post")
                mediaType("application/json") {
                    arraySchema(Post::class)
                }
            }
        }
    }

}

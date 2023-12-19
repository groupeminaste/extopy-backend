package me.nathanfallet.extopy.controllers.users

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*
import io.swagger.v3.oas.models.OpenAPI
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.users.CreateUserPayload
import me.nathanfallet.extopy.models.users.UpdateUserPayload
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.ktorx.extensions.arraySchema
import me.nathanfallet.ktorx.extensions.get
import me.nathanfallet.ktorx.extensions.mediaType
import me.nathanfallet.ktorx.extensions.response
import me.nathanfallet.ktorx.models.api.APIMapping
import me.nathanfallet.ktorx.routers.api.APIModelRouter

class UsersRouter(
    override val controller: IUsersController,
) : APIModelRouter<User, String, CreateUserPayload, UpdateUserPayload>(
    typeInfo<User>(),
    typeInfo<CreateUserPayload>(),
    typeInfo<UpdateUserPayload>(),
    typeInfo<List<User>>(),
    controller,
    mapping = APIMapping(
        listEnabled = false,
        createEnabled = false,
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
            operationId("getUserPostsById")
            addTagsItem("User")
            description("Get user posts by id")
            parameters(getOpenAPIParameters())
            response("200") {
                description("Posts by user")
                mediaType("application/json") {
                    arraySchema(Post::class)
                }
            }
        }
    }

}

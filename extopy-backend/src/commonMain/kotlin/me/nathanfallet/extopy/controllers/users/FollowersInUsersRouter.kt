package me.nathanfallet.extopy.controllers.users

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*
import io.swagger.v3.oas.models.OpenAPI
import me.nathanfallet.extopy.models.users.FollowerInUser
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.ktorx.extensions.arraySchema
import me.nathanfallet.ktorx.extensions.get
import me.nathanfallet.ktorx.extensions.mediaType
import me.nathanfallet.ktorx.extensions.response
import me.nathanfallet.ktorx.models.api.APIMapping
import me.nathanfallet.ktorx.routers.api.APIChildModelRouter

class FollowersInUsersRouter(
    override val controller: IFollowersInUsersController,
    usersRouter: UsersRouter,
) : APIChildModelRouter<FollowerInUser, String, Unit, Unit, User, String>(
    typeInfo<FollowerInUser>(),
    typeInfo<Unit>(),
    typeInfo<Unit>(),
    typeInfo<List<FollowerInUser>>(),
    controller,
    usersRouter,
    mapping = APIMapping(
        getEnabled = false,
        updateEnabled = false
    ),
    route = "followers",
    prefix = "/api/v1"
) {

    override fun createRoutes(root: Route, openAPI: OpenAPI?) {
        super.createRoutes(root, openAPI)
        createAPIGetFollowingRoute(root, openAPI)
    }

    fun createAPIGetFollowingRoute(root: Route, openAPI: OpenAPI?) {
        val followingRoute = fullRoute.replace("followers", "following")
        root.get(followingRoute) {
            try {
                call.respond(controller.listFollowing(call, parentRouter!!.get(call)))
            } catch (exception: Exception) {
                handleExceptionAPI(exception, call)
            }
        }
        openAPI?.get(followingRoute) {
            operationId("listFollowingInUser")
            addTagsItem("FollowerInUser")
            description("Get all FollowingInUser")
            parameters(getOpenAPIParameters())
            response("200") {
                description("List of FollowerInUser")
                mediaType("application/json") {
                    arraySchema(FollowerInUser::class)
                }
            }
        }
    }

}

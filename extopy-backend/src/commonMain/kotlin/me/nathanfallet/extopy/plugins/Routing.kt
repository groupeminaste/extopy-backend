package me.nathanfallet.extopy.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.servers.Server
import me.nathanfallet.extopy.controllers.auth.AuthRouter
import me.nathanfallet.extopy.controllers.posts.LikesInPostsRouter
import me.nathanfallet.extopy.controllers.posts.PostsRouter
import me.nathanfallet.extopy.controllers.timelines.TimelinesRouter
import me.nathanfallet.extopy.controllers.users.FollowersInUsersRouter
import me.nathanfallet.extopy.controllers.users.UsersRouter
import me.nathanfallet.extopy.models.application.ExtopyEnvironment
import me.nathanfallet.ktorx.extensions.info
import me.nathanfallet.ktorx.routers.openapi.OpenAPIRouter
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    install(IgnoreTrailingSlash)
    routing {
        val openAPI = OpenAPI().info {
            this.title = "Extopy API"
            this.description = "Extopy API"
            this.version = "1.0.0"
        }
        openAPI.servers(
            listOf(
                Server().description("Production server").url(ExtopyEnvironment.PRODUCTION.baseUrl),
                Server().description("Staging server").url(ExtopyEnvironment.DEVELOPMENT.baseUrl)
            )
        )

        val authRouter by inject<AuthRouter>()
        val usersRouter by inject<UsersRouter>()
        val followersInUsersRouter by inject<FollowersInUsersRouter>()
        val postsRouter by inject<PostsRouter>()
        val likesInPostsRouter by inject<LikesInPostsRouter>()
        val timelinesRouter by inject<TimelinesRouter>()

        val openAPIRouter = OpenAPIRouter()

        authenticate("api-v1-jwt", optional = true) {
            authRouter.createRoutes(this, openAPI)
            usersRouter.createRoutes(this, openAPI)
            followersInUsersRouter.createRoutes(this, openAPI)
            postsRouter.createRoutes(this, openAPI)
            likesInPostsRouter.createRoutes(this, openAPI)
            timelinesRouter.createRoutes(this, openAPI)

            openAPIRouter.createRoutes(this, openAPI)
        }

        staticResources("", "static")
        get { call.respondTemplate("index.ftl") }
    }
}

package me.nathanfallet.extopy.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
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
import me.nathanfallet.extopy.controllers.web.WebRouter
import me.nathanfallet.extopy.models.application.ExtopyEnvironment
import me.nathanfallet.ktorx.extensions.info
import me.nathanfallet.ktorx.routers.openapi.OpenAPIRouter
import org.koin.ktor.ext.get

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

        authenticate("api-v1-jwt", optional = true) {
            listOf(
                get<WebRouter>(),
                get<AuthRouter>(),
                get<UsersRouter>(),
                get<FollowersInUsersRouter>(),
                get<PostsRouter>(),
                get<LikesInPostsRouter>(),
                get<TimelinesRouter>(),
                OpenAPIRouter(), // OpenAPI should be last
            ).forEach {
                it.createRoutes(this, openAPI)
            }
        }

        staticResources("", "static")
    }
}

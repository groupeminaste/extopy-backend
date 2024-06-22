package com.extopy.plugins

import com.extopy.controllers.auth.AuthRouter
import com.extopy.controllers.posts.LikesInPostsRouter
import com.extopy.controllers.posts.PostsRouter
import com.extopy.controllers.timelines.TimelinesRouter
import com.extopy.controllers.users.FollowersInUsersRouter
import com.extopy.controllers.users.UsersRouter
import com.extopy.controllers.web.WebRouter
import com.extopy.models.application.ExtopyEnvironment
import dev.kaccelero.routers.OpenAPIRouter
import dev.kaccelero.routers.createRoutes
import dev.kaccelero.routers.info
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.servers.Server
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

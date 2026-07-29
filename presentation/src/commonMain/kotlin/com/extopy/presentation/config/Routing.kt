package com.extopy.presentation.config

import com.extopy.presentation.routes.auth.authApiRoutes
import com.extopy.presentation.routes.auth.authWebRoutes
import com.extopy.presentation.routes.health.healthRoutes
import com.extopy.presentation.routes.localized
import com.extopy.presentation.routes.posts.likesInPostsRoutes
import com.extopy.presentation.routes.posts.postsRoutes
import com.extopy.presentation.routes.timelines.timelinesRoutes
import com.extopy.presentation.routes.users.followersInUsersRoutes
import com.extopy.presentation.routes.users.usersRoutes
import com.extopy.presentation.routes.web.webRoutes
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import org.koin.ktor.ext.get

/**
 * The name of the provider validating the tokens of the API clients. It is installed by the
 * infrastructure, which owns how a token is verified; this layer only says which routes sit behind
 * it, so the two ends agree on a name rather than on a type.
 */
private const val API_JWT = "api-v1-jwt"

/**
 * Where the rendered documentation is written on startup.
 */
private val OPENAPI_OUTPUT_PATH = "${System.getProperty("java.io.tmpdir")}/extopy-openapi"

fun Application.configureRouting() {
    install(Resources)
    install(IgnoreTrailingSlash)
    install(AutoHeadResponse)
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        anyHost()
    }
    routing {
        // Probes, which must answer without any authentication
        healthRoutes(get())

        // The token endpoints authenticate on what they are handed, not on a bearer token
        authApiRoutes(get())

        authenticate(API_JWT, optional = true) {
            // API
            usersRoutes(get())
            followersInUsersRoutes(get())
            postsRoutes(get())
            likesInPostsRoutes(get())
            timelinesRoutes(get())

            // Web pages, which are also reachable while signed in with a session cookie
            webRoutes()
            localized("/auth") {
                authWebRoutes(get())
            }
        }

        // The documentation is read off the routing tree above, so it can only ever describe the
        // routes that are actually mounted.
        openAPI(path = "docs") {
            info = OpenApiInfo("Extopy API", "1.0.0")
            source = OpenApiDocSource.Routing { routingRoot.descendants() }
            // The pages are rendered on startup, into a scratch directory rather than into
            // whichever one the process happens to have been started from.
            outputPath = OPENAPI_OUTPUT_PATH
        }

        staticResources("", "static")
    }
}

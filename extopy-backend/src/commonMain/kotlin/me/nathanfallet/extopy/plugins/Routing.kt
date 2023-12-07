package me.nathanfallet.extopy.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import me.nathanfallet.extopy.controllers.auth.AuthRouter
import me.nathanfallet.extopy.controllers.users.UsersRouter
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    routing {
        val authRouter by inject<AuthRouter>()
        val usersRouter by inject<UsersRouter>()

        authenticate("api-v1-jwt", optional = true) {
            authRouter.createRoutes(this)
            usersRouter.createRoutes(this)
        }

        staticResources("", "static")
        get { call.respondTemplate("index.ftl") }
    }
}

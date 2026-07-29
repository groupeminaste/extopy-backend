package com.extopy.presentation.routes.auth

import com.extopy.domain.exceptions.auth.InvalidClientException
import com.extopy.domain.exceptions.auth.InvalidCredentialsException
import com.extopy.domain.exceptions.auth.InvalidEmailCodeException
import com.extopy.domain.models.auth.ClientForUser
import com.extopy.domain.models.auth.LoginPayload
import com.extopy.domain.models.auth.RegisterCodePayload
import com.extopy.domain.usecases.application.CreateCodeInEmailUseCase
import com.extopy.domain.usecases.application.DeleteCodeInEmailUseCase
import com.extopy.domain.usecases.application.GetCodeInEmailUseCase
import com.extopy.domain.usecases.application.SendRegistrationEmailUseCase
import com.extopy.domain.usecases.auth.*
import com.extopy.domain.usecases.users.GetUserUseCase
import com.extopy.presentation.errors.toHttpError
import com.extopy.presentation.extensions.*
import com.extopy.presentation.mappers.auth.toClientForUserView
import com.extopy.presentation.models.SessionPayload
import com.extopy.presentation.views.RegisterView
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.datetime.LocalDate
import kotlin.uuid.Uuid

/**
 * Dependencies required for the authentication pages.
 */
data class AuthWebRoutesDependencies(
    val loginUseCase: LoginUseCase,
    val registerUseCase: RegisterUseCase,
    val createCodeInEmailUseCase: CreateCodeInEmailUseCase,
    val getCodeInEmailUseCase: GetCodeInEmailUseCase,
    val deleteCodeInEmailUseCase: DeleteCodeInEmailUseCase,
    val sendRegistrationEmailUseCase: SendRegistrationEmailUseCase,
    val getClientUseCase: GetClientUseCase,
    val createAuthCodeUseCase: CreateAuthCodeUseCase,
    val getUserUseCase: GetUserUseCase,
)

private const val LOGIN_TEMPLATE = "auth/login.ftl"
private const val REGISTER_TEMPLATE = "auth/register.ftl"
private const val AUTHORIZE_TEMPLATE = "auth/authorize.ftl"
private const val REDIRECT_TEMPLATE = "auth/redirect.ftl"

/**
 * Where to send the visitor once signed in or out. Only same-site paths are honoured, so that a
 * crafted `?redirect=` cannot bounce anyone to another host.
 *
 * A single leading slash is not enough to be safe: a browser strips tabs and newlines before parsing
 * a URL, and reads a backslash as a separator, so `/\host` and `/<tab>/host` both end up pointing at
 * `host`. Anything below a space is therefore refused outright before the shape is checked.
 */
private val SAME_SITE_PATH = Regex("""^/(?![/\\]).*""")

private fun ApplicationCall.redirectTarget(): String = request.queryParameters["redirect"]
    ?.takeIf { target -> target.none { it <= ' ' } && SAME_SITE_PATH.matches(target) }
    ?: "/"

/**
 * Renders a form again with the message of a refusal inline, which is how these pages have always
 * reported one. Anything that is not a refusal is a bug, and is left to the status pages.
 */
private suspend fun ApplicationCall.respondFormError(
    template: String,
    cause: Throwable,
    model: Map<String, Any?> = emptyMap(),
) {
    val error = cause.toHttpError() ?: throw cause
    respondView(template, model + mapOf("error" to error.key), error.status)
}

/**
 * Reads a required field of a submitted form.
 */
private fun Parameters.require(name: String): String =
    get(name)?.takeIf { it.isNotBlank() } ?: throw BadRequestException("Missing field: $name")

/**
 * Sends a visitor that is not signed in to the sign-in page, and back here once it is.
 */
private suspend fun ApplicationCall.redirectToLogin() =
    respondRedirect("/auth/login?redirect=${request.uri}")

/**
 * Configures the authentication pages.
 */
fun Route.authWebRoutes(dependencies: AuthWebRoutesDependencies) = with(dependencies) {
    get("/auth/login") {
        call.respondView(LOGIN_TEMPLATE)
    }
    post("/auth/login") {
        try {
            val parameters = call.receiveParameters()
            val payload = LoginPayload(parameters.require("username"), parameters.require("password"))
            val user = loginUseCase(payload) ?: throw InvalidCredentialsException()
            // Drop whatever session the visitor arrived with, so a planted id cannot survive the
            // sign-in and end up pointing at the account that just used it.
            call.clearSession()
            call.setSession(SessionPayload(user.id))
            call.respondRedirect(call.redirectTarget())
        } catch (cause: Throwable) {
            call.respondFormError(LOGIN_TEMPLATE, cause)
        }
    }
    get("/auth/logout") {
        call.clearSession()
        call.respondRedirect(call.redirectTarget())
    }

    get("/auth/register") {
        call.respondView(REGISTER_TEMPLATE)
    }
    post("/auth/register") {
        try {
            val email = call.receiveParameters().require("email")
            val code = createCodeInEmailUseCase(email) ?: throw InvalidEmailCodeException()
            sendRegistrationEmailUseCase(email, code.code, call.language())
            call.respondView(REGISTER_TEMPLATE, mapOf("success" to "auth_register_code_created"))
        } catch (cause: Throwable) {
            call.respondFormError(REGISTER_TEMPLATE, cause)
        }
    }
    get("/auth/register/{code}") {
        try {
            val code = call.parameters.getOrFail("code")
            val codeInEmail = getCodeInEmailUseCase(code) ?: throw InvalidEmailCodeException()
            call.respondView(REGISTER_TEMPLATE, mapOf("item" to RegisterView(codeInEmail.email)))
        } catch (cause: Throwable) {
            call.respondFormError(REGISTER_TEMPLATE, cause)
        }
    }
    post("/auth/register/{code}") {
        val code = call.parameters.getOrFail("code")
        try {
            val parameters = call.receiveParameters()
            val payload = RegisterCodePayload(
                password = parameters.require("password"),
                username = parameters.require("username"),
                displayName = parameters.require("displayName"),
                birthdate = parameters.require("birthdate").toLocalDateOrFail(),
            )
            val user = registerUseCase(code, payload) ?: throw InvalidEmailCodeException()
            call.clearSession()
            call.setSession(SessionPayload(user.id))
            deleteCodeInEmailUseCase(code)
            call.respondRedirect(call.redirectTarget())
        } catch (cause: Throwable) {
            // The address is put back so that the form the visitor lands on is the filled-in one,
            // not the one asking for an address again.
            val item = getCodeInEmailUseCase(code)?.let { RegisterView(it.email) }
            call.respondFormError(REGISTER_TEMPLATE, cause, mapOf("item" to item))
        }
    }

    get("/auth/authorize") {
        val user = call.userOrNull(getUserUseCase) ?: return@get call.redirectToLogin()
        try {
            val client = call.requireClient(getClientUseCase)
            call.respondView(AUTHORIZE_TEMPLATE, mapOf("item" to ClientForUser(client, user).toClientForUserView()))
        } catch (cause: Throwable) {
            call.respondFormError(AUTHORIZE_TEMPLATE, cause)
        }
    }
    post("/auth/authorize") {
        val user = call.userOrNull(getUserUseCase) ?: return@post call.redirectToLogin()
        try {
            val client = call.requireClient(getClientUseCase)
            val code = createAuthCodeUseCase(ClientForUser(client, user)) ?: throw InvalidClientException()
            call.respondView(
                REDIRECT_TEMPLATE,
                mapOf("redirect" to client.redirectUri.replace("{code}", code))
            )
        } catch (cause: Throwable) {
            call.respondFormError(AUTHORIZE_TEMPLATE, cause)
        }
    }
}

/**
 * Resolves the client of a `?clientId=`, which has to be there and has to exist.
 */
private suspend fun ApplicationCall.requireClient(getClientUseCase: GetClientUseCase) =
    request.queryParameters["clientId"]
        ?.let { runCatching { Uuid.parse(it) }.getOrNull() }
        ?.let { getClientUseCase(it) }
        ?: throw InvalidClientException()

private fun String.toLocalDateOrFail(): LocalDate = runCatching { LocalDate.parse(this) }
    .getOrElse { throw BadRequestException("Malformed date: $this") }

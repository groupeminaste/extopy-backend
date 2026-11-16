package com.extopy.presentation.extensions

import com.extopy.domain.exceptions.auth.InvalidCredentialsException
import com.extopy.domain.models.application.Pagination
import com.extopy.domain.models.users.User
import com.extopy.domain.usecases.users.GetUserUseCase
import com.extopy.presentation.models.SessionPayload
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.freemarker.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import kotlin.uuid.Uuid

/**
 * The languages the message bundle is translated into, the first one being the default.
 */
val SUPPORTED_LANGUAGES = listOf("en", "fr")

/**
 * Resolves the language of the application call.
 *
 * The web pages live under a language prefix, so that is what decides; anything reaching a page
 * without one falls back to what the browser asked for.
 *
 * @return A supported language tag, e.g. `en`.
 */
fun ApplicationCall.language(): String = request.path()
    .trim('/')
    .substringBefore('/')
    .takeIf { it in SUPPORTED_LANGUAGES }
    ?: request.acceptLanguageItems()
        .map { it.value.substringBefore('-').lowercase() }
        .firstOrNull { it in SUPPORTED_LANGUAGES }
    ?: SUPPORTED_LANGUAGES.first()

/**
 * Renders a template.
 *
 * @param template The name of the template.
 * @param model What the template reads, which always carries the language on top.
 * @param status The status to respond with.
 */
suspend fun ApplicationCall.respondView(
    template: String,
    model: Map<String, Any?> = emptyMap(),
    status: HttpStatusCode = HttpStatusCode.OK,
) = respond(status, FreeMarkerContent(template, model + mapOf("locale" to language())))

/**
 * Retrieves the session of the application call.
 */
fun ApplicationCall.session(): SessionPayload? = sessions.get()

/**
 * Starts a session on the application call.
 */
fun ApplicationCall.setSession(payload: SessionPayload) = sessions.set(payload)

/**
 * Ends the session of the application call.
 */
fun ApplicationCall.clearSession() = sessions.clear<SessionPayload>()

/**
 * Retrieves the user ID of the application call, from either of the ways it can be authenticated: a
 * token (the API clients) or a session cookie (the web pages).
 *
 * @return The user ID if present, otherwise null.
 */
fun ApplicationCall.userIdOrNull(): Uuid? =
    principal<JWTPrincipal>()?.payload?.subject?.let { runCatching { Uuid.parse(it) }.getOrNull() }
        ?: session()?.userId

private data class UserForCall(
    val user: User?,
)

private val userKey = AttributeKey<UserForCall>("user")

/**
 * Retrieves the user of the application call, looking it up only once per call.
 *
 * @param getUserUseCase The use case resolving a user from its ID.
 *
 * @return The [User] if the call is authenticated, otherwise null.
 */
suspend fun ApplicationCall.userOrNull(getUserUseCase: GetUserUseCase): User? =
    // Note: we cannot use `computeIfAbsent` because it does not support suspending functions
    attributes.getOrNull(userKey)?.user ?: run {
        val computed = UserForCall(userIdOrNull()?.let { getUserUseCase(it, it) })
        attributes.put(userKey, computed)
        computed.user
    }

/**
 * Retrieves the user of the application call.
 *
 * @param getUserUseCase The use case resolving a user from its ID.
 *
 * @return The [User].
 * @throws InvalidCredentialsException if the call is not authenticated.
 */
suspend fun ApplicationCall.requireUser(getUserUseCase: GetUserUseCase): User =
    userOrNull(getUserUseCase) ?: throw InvalidCredentialsException()

/**
 * Reads the window the caller asked for. A limit or an offset that is not a number is no limit or
 * offset at all, which is what the previous framework did too.
 */
fun ApplicationCall.pagination(): Pagination = Pagination(
    limit = request.queryParameters["limit"]?.toLongOrNull() ?: Pagination.DEFAULT_LIMIT,
    offset = request.queryParameters["offset"]?.toLongOrNull() ?: 0,
    search = request.queryParameters["search"],
)

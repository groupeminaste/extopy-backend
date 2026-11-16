package com.extopy.presentation.errors

import com.extopy.domain.exceptions.auth.*
import com.extopy.domain.exceptions.posts.*
import com.extopy.domain.exceptions.timelines.TimelineNotFoundException
import com.extopy.domain.exceptions.users.*
import com.extopy.domain.exceptions.validation.ValidationException
import io.ktor.http.*
import io.ktor.server.plugins.*

/**
 * A refusal, as both the API and the web pages name it: a status, and the key of the message the
 * bundle and the templates already read.
 */
data class HttpError(
    val status: HttpStatusCode,
    val key: String,
)

/**
 * The single table saying what each failure answers with.
 *
 * The API reads it through the status pages and the web forms read it to render themselves again
 * with the message inline, so the two can never drift. Anything missing from here is a bug rather
 * than a refusal, and comes back as a 500.
 *
 * @return The refusal, or null if this is not one.
 */
fun Throwable.toHttpError(): HttpError? = when (this) {
    // Auth
    is InvalidCredentialsException -> HttpError(HttpStatusCode.Unauthorized, "auth_invalid_credentials")
    is InvalidTokenException -> HttpError(HttpStatusCode.Unauthorized, "auth_invalid_token")
    is InvalidClientException -> HttpError(HttpStatusCode.BadRequest, "auth_invalid_client")
    is InvalidAuthCodeException -> HttpError(HttpStatusCode.BadRequest, "auth_invalid_code")
    is InvalidEmailCodeException -> HttpError(HttpStatusCode.NotFound, "auth_code_invalid")
    is EmailAlreadyTakenException -> HttpError(HttpStatusCode.BadRequest, "auth_register_email_taken")
    is UsernameAlreadyTakenException -> HttpError(HttpStatusCode.BadRequest, "auth_register_username_taken")

    // Users
    is UserNotFoundException -> HttpError(HttpStatusCode.NotFound, "users_not_found")
    is UserUpdateForbiddenException -> HttpError(HttpStatusCode.Forbidden, "users_update_not_allowed")
    is FollowSelfForbiddenException -> HttpError(HttpStatusCode.Forbidden, "followers_in_users_self_not_allowed")
    is FollowerInUserDeleteForbiddenException ->
        HttpError(HttpStatusCode.Forbidden, "followers_in_users_delete_not_allowed")

    // Posts
    is PostNotFoundException -> HttpError(HttpStatusCode.NotFound, "posts_not_found")
    is PostUpdateForbiddenException -> HttpError(HttpStatusCode.Forbidden, "posts_update_not_allowed")
    is PostDeleteForbiddenException -> HttpError(HttpStatusCode.Forbidden, "posts_delete_not_allowed")
    is PostBodyEmptyException -> HttpError(HttpStatusCode.BadRequest, "posts_body_empty")
    is PostReplyAndRepostException -> HttpError(HttpStatusCode.BadRequest, "posts_can_only_one_in_reply_or_repost")
    is RepliedPostNotFoundException -> HttpError(HttpStatusCode.BadRequest, "posts_replied_to_not_found")
    is RepostedPostNotFoundException -> HttpError(HttpStatusCode.BadRequest, "posts_repost_of_not_found")
    is LikeInPostDeleteForbiddenException -> HttpError(HttpStatusCode.Forbidden, "likes_in_posts_delete_not_allowed")

    // Timelines
    is TimelineNotFoundException -> HttpError(HttpStatusCode.NotFound, "timelines_not_found")

    // A field that does not hold up carries its own key, named per form and per field.
    is ValidationException -> HttpError(HttpStatusCode.BadRequest, key)

    // Malformed request bodies and path parameters, which Ktor wraps for us
    is BadRequestException -> HttpError(HttpStatusCode.BadRequest, "error_body_invalid")

    else -> null
}

/**
 * What anything else answers with.
 */
val INTERNAL_ERROR = HttpError(HttpStatusCode.InternalServerError, "error_internal")

package com.extopy.presentation.routes.auth

import com.extopy.api.requests.auth.AuthRequest
import com.extopy.api.requests.auth.RefreshTokenRequest
import com.extopy.api.resources.auth.AuthApi
import com.extopy.domain.exceptions.auth.InvalidAuthCodeException
import com.extopy.domain.usecases.auth.DeleteAuthCodeUseCase
import com.extopy.domain.usecases.auth.GenerateAuthTokenUseCase
import com.extopy.domain.usecases.auth.GetAuthCodeUseCase
import com.extopy.domain.usecases.auth.GetClientForUserForRefreshTokenUseCase
import com.extopy.presentation.mappers.auth.toAuthTokenResponse
import io.ktor.server.request.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Dependencies required for the authentication endpoints of the API.
 */
data class AuthApiRoutesDependencies(
    val getAuthCodeUseCase: GetAuthCodeUseCase,
    val deleteAuthCodeUseCase: DeleteAuthCodeUseCase,
    val generateAuthTokenUseCase: GenerateAuthTokenUseCase,
    val getClientForUserForRefreshTokenUseCase: GetClientForUserForRefreshTokenUseCase,
)

/**
 * Configures the endpoints a client exchanges its codes and tokens on.
 */
fun Route.authApiRoutes(dependencies: AuthApiRoutesDependencies) = with(dependencies) {
    post<AuthApi.Token> {
        val request = call.receive<AuthRequest>()
        val clientForUser = getAuthCodeUseCase(request.code)?.takeIf {
            it.client.id == request.clientId && it.client.secret == request.clientSecret
        } ?: throw InvalidAuthCodeException()
        // A code is good for exactly one exchange, so it is burnt before the tokens are handed out.
        if (!deleteAuthCodeUseCase(request.code)) throw InvalidAuthCodeException()
        call.respond(generateAuthTokenUseCase(clientForUser).toAuthTokenResponse())
    }
    post<AuthApi.Refresh> {
        val request = call.receive<RefreshTokenRequest>()
        val clientForUser = getClientForUserForRefreshTokenUseCase(request.refreshToken)
            ?: throw InvalidAuthCodeException()
        call.respond(generateAuthTokenUseCase(clientForUser).toAuthTokenResponse())
    }
}

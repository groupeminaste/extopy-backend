package com.extopy.infrastructure.config

import com.extopy.domain.exceptions.auth.InvalidTokenException
import com.extopy.infrastructure.jwt.JwtTokenService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.koin.ktor.ext.inject

fun Application.configureSecurity() {
    val tokenService by inject<JwtTokenService>()

    authentication {
        // The API clients, which carry a token
        jwt(API_JWT) {
            verifier(tokenService.verifier)
            validate { JWTPrincipal(it.payload) }
            // Let the status pages own the response shape, rather than writing it a second time.
            challenge { _, _ -> throw InvalidTokenException() }
        }
    }
}

/**
 * The name of the provider validating the tokens of the API clients.
 */
const val API_JWT = "api-v1-jwt"

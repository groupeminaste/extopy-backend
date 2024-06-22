package com.extopy.services.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import java.util.*

class JWTService(
    private val secret: String,
    private val issuer: String,
    private val expiration: Long = 7 * 24 * 60 * 60 * 1000L, // 7 days
    private val refreshExpiration: Long = 30 * 24 * 60 * 60 * 1000L, // 30 days
) : IJWTService {

    override val verifier: JWTVerifier =
        JWT.require(Algorithm.HMAC256(secret))
            .withIssuer(issuer)
            .build()

    override val authenticationFunction: AuthenticationFunction<JWTCredential> = {
        JWTPrincipal(it.payload)
    }

    override val challenge: JWTAuthChallengeFunction = { _, _ ->
        call.response.status(HttpStatusCode.Unauthorized)
        call.respond(mapOf("error" to "auth_invalid_token"))
    }

    override fun generateJWT(userId: String, clientId: String, type: String): String {
        val effectiveExpiration = when (type) {
            "refresh" -> refreshExpiration
            else -> expiration
        }
        return JWT.create()
            .withSubject(userId)
            .withAudience(clientId)
            .withIssuer(issuer)
            .withExpiresAt(Date(System.currentTimeMillis() + effectiveExpiration))
            .sign(Algorithm.HMAC256(secret))
    }

    override fun verifyJWT(token: String): DecodedJWT? =
        try {
            verifier.verify(token)
        } catch (e: JWTVerificationException) {
            null
        }

}

package com.extopy.infrastructure.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.JWTVerifier
import com.extopy.domain.models.auth.TokenPayload
import com.extopy.domain.models.auth.TokenType
import com.extopy.domain.services.TokenService
import java.util.*
import kotlin.time.Duration.Companion.days
import kotlin.uuid.Uuid

/**
 * JWT implementation of [TokenService].
 *
 * The subject is the user, and the audience is the client it authorized, which is what lets a
 * refresh token be traded back for the pair it came from.
 */
class JwtTokenService(
    private val secret: String,
    private val issuer: String,
    private val accessTokenExpiration: Long = 7.days.inWholeMilliseconds,
    private val refreshTokenExpiration: Long = 365.days.inWholeMilliseconds,
) : TokenService {

    /**
     * Exposed so that the Ktor `jwt` provider can validate the incoming tokens with it.
     */
    val verifier: JWTVerifier = JWT.require(Algorithm.HMAC256(secret))
        .withIssuer(issuer)
        .build()

    override fun generateToken(userId: Uuid, clientId: Uuid, type: TokenType): String {
        val expiration = when (type) {
            TokenType.ACCESS -> accessTokenExpiration
            TokenType.REFRESH -> refreshTokenExpiration
        }
        return JWT.create()
            .withSubject(userId.toString())
            .withAudience(clientId.toString())
            .withIssuer(issuer)
            .withExpiresAt(Date(System.currentTimeMillis() + expiration))
            .sign(Algorithm.HMAC256(secret))
    }

    override fun verifyToken(token: String): TokenPayload? = try {
        val decoded = verifier.verify(token)
        decoded.subject?.let { subject ->
            TokenPayload(
                userId = Uuid.parse(subject),
                clientId = decoded.audience?.singleOrNull()?.let(Uuid::parse),
            )
        }
    } catch (exception: JWTVerificationException) {
        null
    } catch (exception: IllegalArgumentException) {
        // A token whose subject or audience is not a UUID is as good as one that does not verify.
        null
    }

}

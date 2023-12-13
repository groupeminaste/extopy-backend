package me.nathanfallet.extopy.services.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

class JWTService(
    private val secret: String,
    private val issuer: String,
    private val expiration: Long = 7 * 24 * 60 * 60 * 1000L, // 7 days
    private val refreshExpiration: Long = 30 * 24 * 60 * 60 * 1000L, // 30 days
) : IJWTService {

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

}

package com.extopy.domain.services

import com.extopy.domain.models.auth.TokenPayload
import com.extopy.domain.models.auth.TokenType
import kotlin.uuid.Uuid

/**
 * Issues and verifies the tokens the API clients carry.
 */
interface TokenService {

    /**
     * Issues a token for a user and the client it authorized.
     *
     * @param type Which token to issue, which decides how long it stays valid.
     */
    fun generateToken(userId: Uuid, clientId: Uuid, type: TokenType): String

    /**
     * Verifies a token.
     *
     * @return What the token says, or null if it does not hold up.
     */
    fun verifyToken(token: String): TokenPayload?

}

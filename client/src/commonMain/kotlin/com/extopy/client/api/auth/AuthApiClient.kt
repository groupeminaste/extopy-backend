package com.extopy.client.api.auth

import com.extopy.api.requests.auth.AuthRequest
import com.extopy.api.responses.auth.AuthTokenResponse

/**
 * The authentication endpoints of the API.
 */
interface AuthApiClient {

    /**
     * Exchanges an authorization code for a pair of tokens, and stores them.
     */
    suspend fun token(request: AuthRequest): AuthTokenResponse

    /**
     * Exchanges the refresh token being held for a fresh pair, and stores them.
     */
    suspend fun refresh(): AuthTokenResponse

    /**
     * Drops the tokens being held.
     */
    suspend fun logout()

}

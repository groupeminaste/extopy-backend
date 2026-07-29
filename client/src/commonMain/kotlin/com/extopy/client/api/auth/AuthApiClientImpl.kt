package com.extopy.client.api.auth

import com.extopy.api.requests.auth.AuthRequest
import com.extopy.api.requests.auth.RefreshTokenRequest
import com.extopy.api.resources.auth.AuthApi
import com.extopy.api.responses.auth.AuthTokenResponse
import com.extopy.client.auth.AuthTokens
import com.extopy.client.auth.TokenStore
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.http.*

class AuthApiClientImpl(
    private val client: HttpClient,
    private val tokenStore: TokenStore,
) : AuthApiClient {

    override suspend fun token(request: AuthRequest): AuthTokenResponse = client
        .post(AuthApi.Token()) {
            withoutBearerToken()
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        .body<AuthTokenResponse>()
        .also { it.store() }

    override suspend fun refresh(): AuthTokenResponse {
        val refreshToken = tokenStore.load()?.refreshToken ?: throw IllegalStateException("Not authorized")
        return client
            .post(AuthApi.Refresh()) {
                withoutBearerToken()
                contentType(ContentType.Application.Json)
                setBody(RefreshTokenRequest(refreshToken))
            }
            .body<AuthTokenResponse>()
            .also { it.store() }
    }

    override suspend fun logout() = tokenStore.clear()

    private suspend fun AuthTokenResponse.store() =
        tokenStore.save(AuthTokens(accessToken, refreshToken))

    /**
     * These two endpoints authenticate on what they carry, not on a bearer token. Sending one would
     * be pointless, and a rejected one would send the client into the refresh flow it is already in.
     */
    private fun HttpRequestBuilder.withoutBearerToken() = attributes.put(AuthCircuitBreaker, Unit)

}

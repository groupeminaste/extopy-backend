package com.extopy.client

import com.extopy.api.Serialization
import com.extopy.api.models.application.ExtopyEnvironment
import com.extopy.api.requests.auth.RefreshTokenRequest
import com.extopy.api.responses.auth.AuthTokenResponse
import com.extopy.client.api.auth.AuthApiClientImpl
import com.extopy.client.api.posts.LikesInPostsApiClientImpl
import com.extopy.client.api.posts.PostsApiClientImpl
import com.extopy.client.api.timelines.TimelinesApiClientImpl
import com.extopy.client.api.users.FollowersInUsersApiClientImpl
import com.extopy.client.api.users.UsersApiClientImpl
import com.extopy.client.auth.AuthTokens
import com.extopy.client.auth.InMemoryTokenStore
import com.extopy.client.auth.TokenStore
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

/**
 * Talks to a deployment of Extopy over HTTP.
 *
 * The access token is attached to every call and refreshed on its own the first time the server
 * turns one down, so a caller only ever deals with the authorization code once.
 *
 * @param baseUrl Which deployment to talk to.
 * @param tokenStore Where the tokens live between two runs.
 * @param clientBuilder How to build the underlying client, which is what the tests replace.
 */
class ExtopyClientImpl(
    baseUrl: String = ExtopyEnvironment.PRODUCTION.baseUrl,
    private val tokenStore: TokenStore = InMemoryTokenStore(),
    clientBuilder: (HttpClientConfig<out HttpClientEngineConfig>.() -> Unit) -> HttpClient = { HttpClient(it) },
) : ExtopyClient {

    private val client = clientBuilder {
        expectSuccess = true
        install(Resources)
        install(ContentNegotiation) {
            json(Serialization.json)
        }
        install(Auth) {
            bearer {
                loadTokens { tokenStore.load()?.toBearerTokens() }
                refreshTokens {
                    val refreshToken = oldTokens?.refreshToken ?: return@refreshTokens null
                    runCatching {
                        client.post("$baseUrl/api/v1/auth/refresh") {
                            markAsRefreshTokenRequest()
                            contentType(ContentType.Application.Json)
                            setBody(RefreshTokenRequest(refreshToken))
                        }.body<AuthTokenResponse>()
                    }.getOrElse {
                        // A refresh token the server no longer accepts is worth nothing, and holding
                        // on to it would have every call fail twice instead of once.
                        tokenStore.clear()
                        return@refreshTokens null
                    }.let {
                        val tokens = AuthTokens(it.accessToken, it.refreshToken)
                        tokenStore.save(tokens)
                        tokens.toBearerTokens()
                    }
                }
            }
        }
        defaultRequest {
            url(baseUrl)
        }
    }

    override val auth = AuthApiClientImpl(client, tokenStore)
    override val users = UsersApiClientImpl(client)
    override val followersInUsers = FollowersInUsersApiClientImpl(client)
    override val posts = PostsApiClientImpl(client)
    override val likesInPosts = LikesInPostsApiClientImpl(client)
    override val timelines = TimelinesApiClientImpl(client)

    private fun AuthTokens.toBearerTokens() = BearerTokens(accessToken, refreshToken)

}

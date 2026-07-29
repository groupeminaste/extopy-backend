package com.extopy

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Pins the surface: every path the previous framework derived from the names of the controllers and
 * of the models is declared now, and this is what says the two are the same set.
 *
 * A route that is not mounted answers with an empty 404, which is what this tells apart from a
 * refusal. What each of them actually does is covered by the route tests of the presentation module.
 */
class HttpSurfaceTest {

    private val userId = "00000000-0000-4000-8000-000000000001"
    private val postId = "00000000-0000-4000-8000-000000000002"
    private val timelineId = "00000000-0000-0000-0000-000000000000"

    private val surface = listOf(
        // Probes
        HttpMethod.Get to "/healthz",
        HttpMethod.Get to "/readyz",

        // Web pages, under a language and without one
        HttpMethod.Get to "/",
        HttpMethod.Get to "/auth/login",
        HttpMethod.Get to "/en/auth/login",
        HttpMethod.Post to "/en/auth/login",
        HttpMethod.Get to "/en/auth/logout",
        HttpMethod.Get to "/en/auth/register",
        HttpMethod.Post to "/en/auth/register",
        HttpMethod.Get to "/en/auth/register/abc",
        HttpMethod.Post to "/en/auth/register/abc",
        HttpMethod.Get to "/en/auth/authorize",
        HttpMethod.Post to "/en/auth/authorize",
        HttpMethod.Get to "/fr/auth/login",

        // Auth
        HttpMethod.Post to "/api/v1/auth/token",
        HttpMethod.Post to "/api/v1/auth/refresh",

        // Users
        HttpMethod.Get to "/api/v1/users",
        HttpMethod.Get to "/api/v1/users/$userId",
        HttpMethod.Put to "/api/v1/users/$userId",
        HttpMethod.Get to "/api/v1/users/$userId/posts",

        // Followers
        HttpMethod.Get to "/api/v1/users/$userId/followers",
        HttpMethod.Get to "/api/v1/users/$userId/following",
        HttpMethod.Post to "/api/v1/users/$userId/followers",
        HttpMethod.Delete to "/api/v1/users/$userId/followers/$userId",

        // Posts
        HttpMethod.Get to "/api/v1/posts",
        HttpMethod.Post to "/api/v1/posts",
        HttpMethod.Get to "/api/v1/posts/$postId",
        HttpMethod.Put to "/api/v1/posts/$postId",
        HttpMethod.Delete to "/api/v1/posts/$postId",
        HttpMethod.Get to "/api/v1/posts/$postId/replies",

        // Likes
        HttpMethod.Get to "/api/v1/posts/$postId/likes",
        HttpMethod.Post to "/api/v1/posts/$postId/likes",
        HttpMethod.Delete to "/api/v1/posts/$postId/likes/$userId",

        // Timelines
        HttpMethod.Get to "/api/v1/timelines/$timelineId",
        HttpMethod.Get to "/api/v1/timelines/$timelineId/posts",

        // Documentation and the assets the pages load
        HttpMethod.Get to "/docs",
        HttpMethod.Get to "/css/auth.css",
        HttpMethod.Get to "/img/logo.png",
    )

    @Test
    fun testEveryRouteIsMounted() = testApplication {
        environment {
            config = ApplicationConfig("application.test.conf")
                .mergeWith(MapApplicationConfig("database.name" to "extopy-surface"))
        }
        application { module() }
        val client = client.config { followRedirects = false }

        surface.forEach { (method, path) ->
            val response = client.request(path) { this.method = method }
            assertTrue(
                response.status != HttpStatusCode.NotFound || response.bodyAsText().isNotEmpty(),
                "$method $path is not mounted"
            )
        }
    }

    /**
     * And nothing else is: a path nobody declared answers with an empty 404.
     */
    @Test
    fun testAnUndeclaredPathIsNotMounted() = testApplication {
        environment {
            config = ApplicationConfig("application.test.conf")
                .mergeWith(MapApplicationConfig("database.name" to "extopy-surface-unknown"))
        }
        application { module() }

        listOf("/api/v1/notifications", "/api/v2/users", "/api/v1/users/$userId/followers/following").forEach { path ->
            val response = client.get(path)
            assertTrue(
                response.status == HttpStatusCode.NotFound && response.bodyAsText().isEmpty(),
                "$path answered ${response.status} with ${response.bodyAsText()}"
            )
        }
    }

}

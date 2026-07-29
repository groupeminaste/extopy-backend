package com.extopy.client

import com.extopy.api.Serialization
import com.extopy.api.requests.auth.AuthRequest
import com.extopy.api.requests.posts.PostRequest
import com.extopy.api.requests.users.UpdateUserRequest
import com.extopy.client.auth.AuthTokens
import com.extopy.client.auth.InMemoryTokenStore
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.uuid.Uuid

/**
 * Pins the requests the client sends.
 *
 * These are the other half of the API contract: the paths here have to be the ones the routes of
 * the server are mounted on, and nothing but a test comparing the two catches it when they drift.
 */
class ExtopyClientImplTest {

    private val userId = Uuid.parse("00000000-0000-4000-8000-000000000001")
    private val postId = Uuid.parse("00000000-0000-4000-8000-000000000002")
    private val timelineId = Uuid.parse("00000000-0000-0000-0000-000000000000")

    private class Recorder {
        val requests = mutableListOf<HttpRequestData>()
        var body: String = "[]"
    }

    private fun client(recorder: Recorder, tokens: AuthTokens? = null) = ExtopyClientImpl(
        baseUrl = "https://extopy.test",
        tokenStore = InMemoryTokenStore(tokens),
    ) { config ->
        HttpClient(MockEngine) {
            config()
            engine {
                addHandler { request ->
                    recorder.requests += request
                    respond(
                        recorder.body,
                        HttpStatusCode.OK,
                        headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    )
                }
            }
        }
    }

    private fun Recorder.pathOf(index: Int = 0) = requests[index].url.encodedPath
    private fun Recorder.queryOf(index: Int = 0) = requests[index].url.encodedQuery

    @Test
    fun testUsersPaths() = runTest {
        val recorder = Recorder()
        val client = client(recorder)

        client.users.list(limit = 10, offset = 20, search = "nathan")
        assertEquals("/api/v1/users", recorder.pathOf())
        assertEquals("limit=10&offset=20&search=nathan", recorder.queryOf())

        recorder.body = """{"id":"$userId","displayName":"Nathan","username":"nathanfallet"}"""
        client.users.get(userId)
        assertEquals("/api/v1/users/$userId", recorder.pathOf(1))

        client.users.update(userId, UpdateUserRequest(biography = "Hi"))
        assertEquals("/api/v1/users/$userId", recorder.pathOf(2))
        assertEquals(HttpMethod.Put, recorder.requests[2].method)

        recorder.body = "[]"
        client.users.listPosts(userId)
        assertEquals("/api/v1/users/$userId/posts", recorder.pathOf(3))
    }

    /**
     * The endpoint listing what an account follows sits next to `followers`, not under it. This is
     * the one place where the previous client and the previous server disagreed — the client asked
     * here and the derived routing answered one level down — so it is worth pinning.
     */
    @Test
    fun testFollowersPaths() = runTest {
        val recorder = Recorder()
        val client = client(recorder)

        client.followersInUsers.list(userId)
        assertEquals("/api/v1/users/$userId/followers", recorder.pathOf())

        client.followersInUsers.listFollowing(userId)
        assertEquals("/api/v1/users/$userId/following", recorder.pathOf(1))

        recorder.body = """{"userId":"$userId","targetId":"$userId"}"""
        client.followersInUsers.create(userId)
        assertEquals("/api/v1/users/$userId/followers", recorder.pathOf(2))
        assertEquals(HttpMethod.Post, recorder.requests[2].method)

        client.followersInUsers.delete(userId, userId)
        assertEquals("/api/v1/users/$userId/followers/$userId", recorder.pathOf(3))
        assertEquals(HttpMethod.Delete, recorder.requests[3].method)
    }

    @Test
    fun testPostsPaths() = runTest {
        val recorder = Recorder()
        val client = client(recorder)

        client.posts.list()
        assertEquals("/api/v1/posts", recorder.pathOf())
        assertEquals("", recorder.queryOf())

        recorder.body = """{"id":"$postId","userId":"$userId","publishedAt":"2026-01-02T03:04:05Z"}"""
        client.posts.create(PostRequest("Hello"))
        assertEquals("/api/v1/posts", recorder.pathOf(1))
        assertEquals(HttpMethod.Post, recorder.requests[1].method)

        client.posts.get(postId)
        assertEquals("/api/v1/posts/$postId", recorder.pathOf(2))

        client.posts.update(postId, PostRequest("Hello again"))
        assertEquals(HttpMethod.Put, recorder.requests[3].method)

        client.posts.delete(postId)
        assertEquals(HttpMethod.Delete, recorder.requests[4].method)

        recorder.body = "[]"
        client.posts.listReplies(postId)
        assertEquals("/api/v1/posts/$postId/replies", recorder.pathOf(5))

        client.likesInPosts.list(postId)
        assertEquals("/api/v1/posts/$postId/likes", recorder.pathOf(6))

        client.likesInPosts.delete(postId, userId)
        assertEquals("/api/v1/posts/$postId/likes/$userId", recorder.pathOf(7))
    }

    @Test
    fun testTimelinesPaths() = runTest {
        val recorder = Recorder()
        val client = client(recorder)

        recorder.body = """{"id":"$timelineId"}"""
        client.timelines.get(timelineId)
        assertEquals("/api/v1/timelines/$timelineId", recorder.pathOf())

        recorder.body = "[]"
        client.timelines.listPosts(timelineId, limit = 5)
        assertEquals("/api/v1/timelines/$timelineId/posts", recorder.pathOf(1))
        assertEquals("limit=5", recorder.queryOf(1))
    }

    /**
     * The access token rides along on every call, but not on the two endpoints that hand them out.
     */
    @Test
    fun testTokenIsAttachedToEveryCallButTheAuthOnes() = runTest {
        val recorder = Recorder()
        val client = client(recorder, AuthTokens("access", "refresh"))

        client.posts.list()
        assertEquals("Bearer access", recorder.requests[0].headers[HttpHeaders.Authorization])

        recorder.body = """{"accessToken":"a2","refreshToken":"r2","idToken":"$userId"}"""
        client.auth.refresh()
        assertEquals("/api/v1/auth/refresh", recorder.pathOf(1))
        assertNull(recorder.requests[1].headers[HttpHeaders.Authorization])
    }

    /**
     * Exchanging an authorization code leaves the client holding the pair it was handed.
     */
    @Test
    fun testTokenExchangeStoresWhatItGetsBack() = runTest {
        val recorder = Recorder()
        val store = InMemoryTokenStore()
        val client = ExtopyClientImpl(baseUrl = "https://extopy.test", tokenStore = store) { config ->
            HttpClient(MockEngine) {
                config()
                engine {
                    addHandler { request ->
                        recorder.requests += request
                        respond(
                            """{"accessToken":"a","refreshToken":"r","idToken":"$userId"}""",
                            HttpStatusCode.OK,
                            headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                        )
                    }
                }
            }
        }

        client.auth.token(AuthRequest(userId, "secret", "code"))

        assertEquals("/api/v1/auth/token", recorder.pathOf())
        assertEquals(AuthTokens("a", "r"), store.load())

        client.auth.logout()
        assertNull(store.load())
    }

    /**
     * The serializer both sides agree on leaves the fields that were not read out of the payload.
     */
    @Test
    fun testNullsAreLeftOutOfTheWire() {
        val encoded = Serialization.json.encodeToString(UpdateUserRequest(biography = "Hi"))
        assertEquals("""{"biography":"Hi"}""", encoded)
    }

}

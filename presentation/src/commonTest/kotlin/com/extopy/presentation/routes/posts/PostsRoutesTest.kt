package com.extopy.presentation.routes.posts

import com.extopy.domain.exceptions.posts.PostBodyEmptyException
import com.extopy.domain.models.application.Pagination
import com.extopy.domain.models.posts.PostPayload
import com.extopy.domain.usecases.posts.*
import com.extopy.domain.usecases.users.GetUserUseCase
import com.extopy.presentation.routes.RoutesTestHelper
import com.extopy.presentation.routes.RoutesTestHelper.configureTestApplication
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class PostsRoutesTest {

    private class Mocks {
        val listPostsUseCase = mockk<ListPostsUseCase>()
        val getPostUseCase = mockk<GetPostUseCase>()
        val createPostUseCase = mockk<CreatePostUseCase>()
        val updatePostUseCase = mockk<UpdatePostUseCase>()
        val deletePostUseCase = mockk<DeletePostUseCase>()
        val getPostRepliesUseCase = mockk<GetPostRepliesUseCase>()
        val getUserUseCase = mockk<GetUserUseCase>()
    }

    private fun ApplicationTestBuilder.configureApp(mocks: Mocks) {
        application {
            configureTestApplication(mocks.getUserUseCase)
            routing {
                authenticate(RoutesTestHelper.API_JWT, optional = true) {
                    postsRoutes(
                        PostsRoutesDependencies(
                            mocks.listPostsUseCase,
                            mocks.getPostUseCase,
                            mocks.createPostUseCase,
                            mocks.updatePostUseCase,
                            mocks.deletePostUseCase,
                            mocks.getPostRepliesUseCase,
                            mocks.getUserUseCase,
                        )
                    )
                }
            }
        }
    }

    private fun HttpRequestBuilder.authenticated() =
        header(HttpHeaders.Authorization, "Bearer ${RoutesTestHelper.generateTestToken()}")

    private fun Mocks.signedIn() {
        coEvery { getUserUseCase(RoutesTestHelper.TEST_USER_ID, RoutesTestHelper.TEST_USER_ID) } returns
                RoutesTestHelper.testUser
    }

    @Test
    fun testList() = testApplication {
        val mocks = Mocks()
        mocks.signedIn()
        coEvery { mocks.listPostsUseCase(any(), any()) } returns listOf(RoutesTestHelper.testPost)
        configureApp(mocks)

        val response = client.get("/api/v1/posts?search=hello") { authenticated() }

        assertEquals(HttpStatusCode.OK, response.status)
        assertContains(response.bodyAsText(), "Hello world!")
        coVerify { mocks.listPostsUseCase(Pagination(25, 0, "hello"), RoutesTestHelper.TEST_USER_ID) }
    }

    @Test
    fun testGetUnknownPost() = testApplication {
        val mocks = Mocks()
        mocks.signedIn()
        coEvery { mocks.getPostUseCase(any(), any()) } returns null
        configureApp(mocks)

        val response = client.get("/api/v1/posts/${RoutesTestHelper.TEST_POST_ID}") { authenticated() }

        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("""{"error":"posts_not_found"}""", response.bodyAsText())
    }

    /**
     * Publishing answers 201, which is what the previous framework did for every create.
     */
    @Test
    fun testCreate() = testApplication {
        val mocks = Mocks()
        mocks.signedIn()
        coEvery {
            mocks.createPostUseCase(PostPayload("Hello world!"), RoutesTestHelper.TEST_USER_ID)
        } returns RoutesTestHelper.testPost
        configureApp(mocks)

        val response = client.post("/api/v1/posts") {
            authenticated()
            contentType(ContentType.Application.Json)
            setBody("""{"body":"Hello world!"}""")
        }

        assertEquals(HttpStatusCode.Created, response.status)
        assertContains(response.bodyAsText(), "Hello world!")
    }

    @Test
    fun testCreateWithAnEmptyBody() = testApplication {
        val mocks = Mocks()
        mocks.signedIn()
        coEvery { mocks.createPostUseCase(any(), any()) } throws PostBodyEmptyException()
        configureApp(mocks)

        val response = client.post("/api/v1/posts") {
            authenticated()
            contentType(ContentType.Application.Json)
            setBody("""{"body":"  "}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("""{"error":"posts_body_empty"}""", response.bodyAsText())
    }

    @Test
    fun testCreateWithAMalformedBody() = testApplication {
        val mocks = Mocks()
        mocks.signedIn()
        configureApp(mocks)

        val response = client.post("/api/v1/posts") {
            authenticated()
            contentType(ContentType.Application.Json)
            setBody("""{"nope":1}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("""{"error":"error_body_invalid"}""", response.bodyAsText())
    }

    /**
     * Reading the post back after an update has to carry the account doing it, or the query returns
     * nothing and a write that went through reads as a failure.
     */
    @Test
    fun testUpdate() = testApplication {
        val mocks = Mocks()
        mocks.signedIn()
        coEvery {
            mocks.getPostUseCase(RoutesTestHelper.TEST_POST_ID, RoutesTestHelper.TEST_USER_ID)
        } returns RoutesTestHelper.testPost
        coEvery {
            mocks.updatePostUseCase(
                RoutesTestHelper.TEST_POST_ID,
                PostPayload("Hello again"),
                RoutesTestHelper.TEST_USER_ID
            )
        } returns RoutesTestHelper.testPost.copy(body = "Hello again")
        configureApp(mocks)

        val response = client.put("/api/v1/posts/${RoutesTestHelper.TEST_POST_ID}") {
            authenticated()
            contentType(ContentType.Application.Json)
            setBody("""{"body":"Hello again"}""")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertContains(response.bodyAsText(), "Hello again")
    }

    @Test
    fun testUpdateOfSomeoneElsesPost() = testApplication {
        val mocks = Mocks()
        mocks.signedIn()
        coEvery {
            mocks.getPostUseCase(RoutesTestHelper.TEST_POST_ID, RoutesTestHelper.TEST_USER_ID)
        } returns RoutesTestHelper.testPost.copy(userId = RoutesTestHelper.OTHER_USER_ID)
        configureApp(mocks)

        val response = client.put("/api/v1/posts/${RoutesTestHelper.TEST_POST_ID}") {
            authenticated()
            contentType(ContentType.Application.Json)
            setBody("""{"body":"Hello again"}""")
        }

        assertEquals(HttpStatusCode.Forbidden, response.status)
        assertEquals("""{"error":"posts_update_not_allowed"}""", response.bodyAsText())
    }

    @Test
    fun testDelete() = testApplication {
        val mocks = Mocks()
        mocks.signedIn()
        coEvery {
            mocks.getPostUseCase(RoutesTestHelper.TEST_POST_ID, RoutesTestHelper.TEST_USER_ID)
        } returns RoutesTestHelper.testPost
        coEvery { mocks.deletePostUseCase(RoutesTestHelper.TEST_POST_ID) } returns true
        configureApp(mocks)

        val response = client.delete("/api/v1/posts/${RoutesTestHelper.TEST_POST_ID}") { authenticated() }

        assertEquals(HttpStatusCode.NoContent, response.status)
        coVerify { mocks.deletePostUseCase(RoutesTestHelper.TEST_POST_ID) }
    }

    @Test
    fun testDeleteOfSomeoneElsesPost() = testApplication {
        val mocks = Mocks()
        mocks.signedIn()
        coEvery {
            mocks.getPostUseCase(RoutesTestHelper.TEST_POST_ID, RoutesTestHelper.TEST_USER_ID)
        } returns RoutesTestHelper.testPost.copy(userId = RoutesTestHelper.OTHER_USER_ID)
        configureApp(mocks)

        val response = client.delete("/api/v1/posts/${RoutesTestHelper.TEST_POST_ID}") { authenticated() }

        assertEquals(HttpStatusCode.Forbidden, response.status)
        assertEquals("""{"error":"posts_delete_not_allowed"}""", response.bodyAsText())
    }

    @Test
    fun testListReplies() = testApplication {
        val mocks = Mocks()
        mocks.signedIn()
        coEvery {
            mocks.getPostUseCase(RoutesTestHelper.TEST_POST_ID, RoutesTestHelper.TEST_USER_ID)
        } returns RoutesTestHelper.testPost
        coEvery {
            mocks.getPostRepliesUseCase(RoutesTestHelper.TEST_POST_ID, any(), RoutesTestHelper.TEST_USER_ID)
        } returns listOf(RoutesTestHelper.testPost.copy(body = "A reply"))
        configureApp(mocks)

        val response = client.get("/api/v1/posts/${RoutesTestHelper.TEST_POST_ID}/replies") { authenticated() }

        assertEquals(HttpStatusCode.OK, response.status)
        assertContains(response.bodyAsText(), "A reply")
    }

}

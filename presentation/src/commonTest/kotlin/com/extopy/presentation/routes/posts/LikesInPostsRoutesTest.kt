package com.extopy.presentation.routes.posts

import com.extopy.domain.usecases.posts.CreateLikeInPostUseCase
import com.extopy.domain.usecases.posts.DeleteLikeInPostUseCase
import com.extopy.domain.usecases.posts.GetPostUseCase
import com.extopy.domain.usecases.posts.ListLikesInPostUseCase
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

class LikesInPostsRoutesTest {

    private class Mocks {
        val listLikesInPostUseCase = mockk<ListLikesInPostUseCase>()
        val createLikeInPostUseCase = mockk<CreateLikeInPostUseCase>()
        val deleteLikeInPostUseCase = mockk<DeleteLikeInPostUseCase>()
        val getPostUseCase = mockk<GetPostUseCase>()
        val getUserUseCase = mockk<GetUserUseCase>()
    }

    private fun ApplicationTestBuilder.configureApp(mocks: Mocks) {
        application {
            configureTestApplication(mocks.getUserUseCase)
            routing {
                authenticate(RoutesTestHelper.API_JWT, optional = true) {
                    likesInPostsRoutes(
                        LikesInPostsRoutesDependencies(
                            mocks.listLikesInPostUseCase,
                            mocks.createLikeInPostUseCase,
                            mocks.deleteLikeInPostUseCase,
                            mocks.getPostUseCase,
                            mocks.getUserUseCase,
                        )
                    )
                }
            }
        }
    }

    private fun HttpRequestBuilder.authenticated() =
        header(HttpHeaders.Authorization, "Bearer ${RoutesTestHelper.generateTestToken()}")

    private fun Mocks.signedInOnAPost() {
        coEvery { getUserUseCase(RoutesTestHelper.TEST_USER_ID, RoutesTestHelper.TEST_USER_ID) } returns
                RoutesTestHelper.testUser
        coEvery { getPostUseCase(RoutesTestHelper.TEST_POST_ID, RoutesTestHelper.TEST_USER_ID) } returns
                RoutesTestHelper.testPost
    }

    @Test
    fun testList() = testApplication {
        val mocks = Mocks()
        mocks.signedInOnAPost()
        coEvery { mocks.listLikesInPostUseCase(any(), RoutesTestHelper.TEST_POST_ID) } returns
                listOf(RoutesTestHelper.testLike)
        configureApp(mocks)

        val response = client.get("/api/v1/posts/${RoutesTestHelper.TEST_POST_ID}/likes") { authenticated() }

        assertEquals(HttpStatusCode.OK, response.status)
        assertContains(response.bodyAsText(), "nathanfallet")
    }

    /**
     * The likes of a post nobody can read are not readable either.
     */
    @Test
    fun testListOfAnUnknownPost() = testApplication {
        val mocks = Mocks()
        coEvery { mocks.getUserUseCase(any(), any()) } returns RoutesTestHelper.testUser
        coEvery { mocks.getPostUseCase(any(), any()) } returns null
        configureApp(mocks)

        val response = client.get("/api/v1/posts/${RoutesTestHelper.TEST_POST_ID}/likes") { authenticated() }

        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("""{"error":"posts_not_found"}""", response.bodyAsText())
    }

    @Test
    fun testCreate() = testApplication {
        val mocks = Mocks()
        mocks.signedInOnAPost()
        coEvery {
            mocks.createLikeInPostUseCase(RoutesTestHelper.TEST_POST_ID, RoutesTestHelper.TEST_USER_ID)
        } returns RoutesTestHelper.testLike
        configureApp(mocks)

        val response = client.post("/api/v1/posts/${RoutesTestHelper.TEST_POST_ID}/likes") { authenticated() }

        assertEquals(HttpStatusCode.Created, response.status)
    }

    @Test
    fun testDelete() = testApplication {
        val mocks = Mocks()
        mocks.signedInOnAPost()
        coEvery {
            mocks.deleteLikeInPostUseCase(RoutesTestHelper.TEST_POST_ID, RoutesTestHelper.TEST_USER_ID)
        } returns true
        configureApp(mocks)

        val response = client.delete(
            "/api/v1/posts/${RoutesTestHelper.TEST_POST_ID}/likes/${RoutesTestHelper.TEST_USER_ID}"
        ) { authenticated() }

        assertEquals(HttpStatusCode.NoContent, response.status)
        coVerify {
            mocks.deleteLikeInPostUseCase(RoutesTestHelper.TEST_POST_ID, RoutesTestHelper.TEST_USER_ID)
        }
    }

    /**
     * A like belongs to whoever left it, and only they may take it back.
     */
    @Test
    fun testDeleteOfSomeoneElsesLike() = testApplication {
        val mocks = Mocks()
        mocks.signedInOnAPost()
        configureApp(mocks)

        val response = client.delete(
            "/api/v1/posts/${RoutesTestHelper.TEST_POST_ID}/likes/${RoutesTestHelper.OTHER_USER_ID}"
        ) { authenticated() }

        assertEquals(HttpStatusCode.Forbidden, response.status)
        assertEquals("""{"error":"likes_in_posts_delete_not_allowed"}""", response.bodyAsText())
    }

}

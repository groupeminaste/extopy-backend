package com.extopy.presentation.routes.users

import com.extopy.domain.models.application.Pagination
import com.extopy.domain.models.users.UpdateUserPayload
import com.extopy.domain.usecases.users.GetUserPostsUseCase
import com.extopy.domain.usecases.users.GetUserUseCase
import com.extopy.domain.usecases.users.ListUsersUseCase
import com.extopy.domain.usecases.users.UpdateUserUseCase
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

class UsersRoutesTest {

    private class Mocks {
        val listUsersUseCase = mockk<ListUsersUseCase>()
        val getUserUseCase = mockk<GetUserUseCase>()
        val updateUserUseCase = mockk<UpdateUserUseCase>()
        val getUserPostsUseCase = mockk<GetUserPostsUseCase>()
    }

    private fun ApplicationTestBuilder.configureApp(mocks: Mocks) {
        application {
            configureTestApplication(mocks.getUserUseCase)
            routing {
                authenticate(RoutesTestHelper.API_JWT, optional = true) {
                    usersRoutes(
                        UsersRoutesDependencies(
                            mocks.listUsersUseCase,
                            mocks.getUserUseCase,
                            mocks.updateUserUseCase,
                            mocks.getUserPostsUseCase,
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
    fun testListRequiresAuthentication() = testApplication {
        val mocks = Mocks()
        configureApp(mocks)

        val response = client.get("/api/v1/users")

        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals("""{"error":"auth_invalid_credentials"}""", response.bodyAsText())
    }

    /**
     * The window and the search reach the use case as the caller wrote them, and a missing one
     * falls back to the default page size rather than to no limit at all.
     */
    @Test
    fun testListPassesThePaginationAlong() = testApplication {
        val mocks = Mocks()
        mocks.signedIn()
        coEvery { mocks.listUsersUseCase(any(), any()) } returns listOf(RoutesTestHelper.otherUser)
        configureApp(mocks)

        val response = client.get("/api/v1/users?limit=10&offset=5&search=some") { authenticated() }

        assertEquals(HttpStatusCode.OK, response.status)
        assertContains(response.bodyAsText(), "someoneelse")
        coVerify {
            mocks.listUsersUseCase(Pagination(10, 5, "some"), RoutesTestHelper.TEST_USER_ID)
        }
    }

    @Test
    fun testListFallsBackToTheDefaultWindow() = testApplication {
        val mocks = Mocks()
        mocks.signedIn()
        coEvery { mocks.listUsersUseCase(any(), any()) } returns emptyList()
        configureApp(mocks)

        client.get("/api/v1/users") { authenticated() }

        coVerify { mocks.listUsersUseCase(Pagination(25, 0, null), RoutesTestHelper.TEST_USER_ID) }
    }

    @Test
    fun testGet() = testApplication {
        val mocks = Mocks()
        mocks.signedIn()
        coEvery {
            mocks.getUserUseCase(RoutesTestHelper.OTHER_USER_ID, RoutesTestHelper.TEST_USER_ID)
        } returns RoutesTestHelper.otherUser
        configureApp(mocks)

        val response = client.get("/api/v1/users/${RoutesTestHelper.OTHER_USER_ID}") { authenticated() }

        assertEquals(HttpStatusCode.OK, response.status)
        assertContains(response.bodyAsText(), """"username":"someoneelse"""")
    }

    @Test
    fun testGetUnknownUser() = testApplication {
        val mocks = Mocks()
        mocks.signedIn()
        coEvery {
            mocks.getUserUseCase(RoutesTestHelper.OTHER_USER_ID, RoutesTestHelper.TEST_USER_ID)
        } returns null
        configureApp(mocks)

        val response = client.get("/api/v1/users/${RoutesTestHelper.OTHER_USER_ID}") { authenticated() }

        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("""{"error":"users_not_found"}""", response.bodyAsText())
    }

    /**
     * The password hash has no field on the response, so it cannot leave even when a query read it.
     */
    @Test
    fun testGetNeverExposesThePassword() = testApplication {
        val mocks = Mocks()
        mocks.signedIn()
        coEvery {
            mocks.getUserUseCase(RoutesTestHelper.OTHER_USER_ID, RoutesTestHelper.TEST_USER_ID)
        } returns RoutesTestHelper.otherUser.copy(password = "\$2a\$12\$hash", email = "someone@extopy.com")
        configureApp(mocks)

        val body = client.get("/api/v1/users/${RoutesTestHelper.OTHER_USER_ID}") { authenticated() }.bodyAsText()

        assertEquals(false, body.contains("password"), "the response carried the password: $body")
        assertEquals(false, body.contains("hash"), "the response carried the hash: $body")
    }

    @Test
    fun testUpdate() = testApplication {
        val mocks = Mocks()
        mocks.signedIn()
        coEvery {
            mocks.updateUserUseCase(RoutesTestHelper.TEST_USER_ID, UpdateUserPayload(biography = "Hi"))
        } returns RoutesTestHelper.testUser.copy(biography = "Hi")
        configureApp(mocks)

        val response = client.put("/api/v1/users/${RoutesTestHelper.TEST_USER_ID}") {
            authenticated()
            contentType(ContentType.Application.Json)
            setBody("""{"biography":"Hi"}""")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertContains(response.bodyAsText(), """"biography":"Hi"""")
    }

    /**
     * An account is the only one allowed to change itself, and that is checked before the body is
     * even read.
     */
    @Test
    fun testUpdateOfAnotherAccountIsRefused() = testApplication {
        val mocks = Mocks()
        mocks.signedIn()
        configureApp(mocks)

        val response = client.put("/api/v1/users/${RoutesTestHelper.OTHER_USER_ID}") {
            authenticated()
            contentType(ContentType.Application.Json)
            setBody("""{"biography":"Hi"}""")
        }

        assertEquals(HttpStatusCode.Forbidden, response.status)
        assertEquals("""{"error":"users_update_not_allowed"}""", response.bodyAsText())
    }

    @Test
    fun testListPosts() = testApplication {
        val mocks = Mocks()
        mocks.signedIn()
        coEvery {
            mocks.getUserUseCase(RoutesTestHelper.OTHER_USER_ID, RoutesTestHelper.TEST_USER_ID)
        } returns RoutesTestHelper.otherUser
        coEvery {
            mocks.getUserPostsUseCase(RoutesTestHelper.OTHER_USER_ID, any(), RoutesTestHelper.TEST_USER_ID)
        } returns listOf(RoutesTestHelper.testPost)
        configureApp(mocks)

        val response = client.get("/api/v1/users/${RoutesTestHelper.OTHER_USER_ID}/posts") { authenticated() }

        assertEquals(HttpStatusCode.OK, response.status)
        assertContains(response.bodyAsText(), "Hello world!")
    }

}

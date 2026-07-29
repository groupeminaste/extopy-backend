package com.extopy.presentation.routes.users

import com.extopy.domain.exceptions.users.FollowSelfForbiddenException
import com.extopy.domain.usecases.users.*
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
import kotlin.test.assertEquals

class FollowersInUsersRoutesTest {

    private class Mocks {
        val listFollowersInUserUseCase = mockk<ListFollowersInUserUseCase>()
        val listFollowingInUserUseCase = mockk<ListFollowingInUserUseCase>()
        val createFollowerInUserUseCase = mockk<CreateFollowerInUserUseCase>()
        val deleteFollowerInUserUseCase = mockk<DeleteFollowerInUserUseCase>()
        val getUserUseCase = mockk<GetUserUseCase>()
    }

    private fun ApplicationTestBuilder.configureApp(mocks: Mocks) {
        application {
            configureTestApplication(mocks.getUserUseCase)
            routing {
                authenticate(RoutesTestHelper.API_JWT, optional = true) {
                    followersInUsersRoutes(
                        FollowersInUsersRoutesDependencies(
                            mocks.listFollowersInUserUseCase,
                            mocks.listFollowingInUserUseCase,
                            mocks.createFollowerInUserUseCase,
                            mocks.deleteFollowerInUserUseCase,
                            mocks.getUserUseCase,
                        )
                    )
                }
            }
        }
    }

    private fun HttpRequestBuilder.authenticated() =
        header(HttpHeaders.Authorization, "Bearer ${RoutesTestHelper.generateTestToken()}")

    private fun Mocks.signedInOnTarget() {
        coEvery { getUserUseCase(RoutesTestHelper.TEST_USER_ID, RoutesTestHelper.TEST_USER_ID) } returns
                RoutesTestHelper.testUser
        coEvery { getUserUseCase(RoutesTestHelper.OTHER_USER_ID, RoutesTestHelper.TEST_USER_ID) } returns
                RoutesTestHelper.otherUser
    }

    private fun followersPath(suffix: String = "") =
        "/api/v1/users/${RoutesTestHelper.OTHER_USER_ID}/followers$suffix"

    @Test
    fun testList() = testApplication {
        val mocks = Mocks()
        mocks.signedInOnTarget()
        coEvery { mocks.listFollowersInUserUseCase(any(), RoutesTestHelper.OTHER_USER_ID) } returns
                listOf(RoutesTestHelper.testFollower)
        configureApp(mocks)

        val response = client.get(followersPath()) { authenticated() }

        assertEquals(HttpStatusCode.OK, response.status)
        coVerify { mocks.listFollowersInUserUseCase(any(), RoutesTestHelper.OTHER_USER_ID) }
    }

    /**
     * Listing what an account follows lives under `followers`, which is where the derived routing of
     * the previous framework put it. Moving it would break every client already calling it.
     */
    @Test
    fun testListFollowingSitsUnderFollowers() = testApplication {
        val mocks = Mocks()
        mocks.signedInOnTarget()
        coEvery { mocks.listFollowingInUserUseCase(any(), RoutesTestHelper.OTHER_USER_ID) } returns
                listOf(RoutesTestHelper.testFollower)
        configureApp(mocks)

        val response = client.get(followersPath("/following")) { authenticated() }

        assertEquals(HttpStatusCode.OK, response.status)
        coVerify { mocks.listFollowingInUserUseCase(any(), RoutesTestHelper.OTHER_USER_ID) }
    }

    @Test
    fun testListOfAnUnknownUser() = testApplication {
        val mocks = Mocks()
        coEvery { mocks.getUserUseCase(RoutesTestHelper.TEST_USER_ID, any()) } returns RoutesTestHelper.testUser
        coEvery { mocks.getUserUseCase(RoutesTestHelper.OTHER_USER_ID, any()) } returns null
        configureApp(mocks)

        val response = client.get(followersPath()) { authenticated() }

        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("""{"error":"users_not_found"}""", response.bodyAsText())
    }

    @Test
    fun testCreate() = testApplication {
        val mocks = Mocks()
        mocks.signedInOnTarget()
        coEvery {
            mocks.createFollowerInUserUseCase(RoutesTestHelper.OTHER_USER_ID, RoutesTestHelper.TEST_USER_ID)
        } returns RoutesTestHelper.testFollower
        configureApp(mocks)

        val response = client.post(followersPath()) { authenticated() }

        assertEquals(HttpStatusCode.Created, response.status)
    }

    @Test
    fun testFollowingYourself() = testApplication {
        val mocks = Mocks()
        mocks.signedInOnTarget()
        coEvery { mocks.createFollowerInUserUseCase(any(), any()) } throws FollowSelfForbiddenException()
        configureApp(mocks)

        val response = client.post(followersPath()) { authenticated() }

        assertEquals(HttpStatusCode.Forbidden, response.status)
        assertEquals("""{"error":"followers_in_users_self_not_allowed"}""", response.bodyAsText())
    }

    /**
     * Unfollowing: the caller is the follower being removed.
     */
    @Test
    fun testDeleteYourOwnLink() = testApplication {
        val mocks = Mocks()
        mocks.signedInOnTarget()
        coEvery {
            mocks.deleteFollowerInUserUseCase(RoutesTestHelper.TEST_USER_ID, RoutesTestHelper.OTHER_USER_ID)
        } returns true
        configureApp(mocks)

        val response = client.delete(followersPath("/${RoutesTestHelper.TEST_USER_ID}")) { authenticated() }

        assertEquals(HttpStatusCode.NoContent, response.status)
    }

    /**
     * Dropping one of your own followers: the caller is the account being followed.
     */
    @Test
    fun testDeleteOneOfYourFollowers() = testApplication {
        val mocks = Mocks()
        coEvery { mocks.getUserUseCase(RoutesTestHelper.TEST_USER_ID, RoutesTestHelper.TEST_USER_ID) } returns
                RoutesTestHelper.testUser
        coEvery {
            mocks.deleteFollowerInUserUseCase(RoutesTestHelper.OTHER_USER_ID, RoutesTestHelper.TEST_USER_ID)
        } returns true
        configureApp(mocks)

        val response = client.delete(
            "/api/v1/users/${RoutesTestHelper.TEST_USER_ID}/followers/${RoutesTestHelper.OTHER_USER_ID}"
        ) { authenticated() }

        assertEquals(HttpStatusCode.NoContent, response.status)
    }

    /**
     * A link between two other accounts is none of the caller's business.
     */
    @Test
    fun testDeleteOfSomeoneElsesLink() = testApplication {
        val mocks = Mocks()
        mocks.signedInOnTarget()
        configureApp(mocks)

        val response = client.delete(followersPath("/${RoutesTestHelper.TEST_CLIENT_ID}")) { authenticated() }

        assertEquals(HttpStatusCode.Forbidden, response.status)
        assertEquals("""{"error":"followers_in_users_delete_not_allowed"}""", response.bodyAsText())
    }

}

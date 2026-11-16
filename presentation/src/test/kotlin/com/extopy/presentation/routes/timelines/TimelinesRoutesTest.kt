package com.extopy.presentation.routes.timelines

import com.extopy.domain.models.timelines.Timeline
import com.extopy.domain.usecases.timelines.GetTimelinePostsUseCase
import com.extopy.domain.usecases.timelines.GetTimelineUseCase
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
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class TimelinesRoutesTest {

    private class Mocks {
        val getTimelineUseCase = mockk<GetTimelineUseCase>()
        val getTimelinePostsUseCase = mockk<GetTimelinePostsUseCase>()
        val getUserUseCase = mockk<GetUserUseCase>()
    }

    private fun ApplicationTestBuilder.configureApp(mocks: Mocks) {
        application {
            configureTestApplication(mocks.getUserUseCase)
            routing {
                authenticate(RoutesTestHelper.API_JWT, optional = true) {
                    timelinesRoutes(
                        TimelinesRoutesDependencies(
                            mocks.getTimelineUseCase,
                            mocks.getTimelinePostsUseCase,
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
    fun testGet() = testApplication {
        val mocks = Mocks()
        mocks.signedIn()
        coEvery { mocks.getTimelineUseCase(Timeline.DEFAULT_ID) } returns RoutesTestHelper.testTimeline
        configureApp(mocks)

        val response = client.get("/api/v1/timelines/${Timeline.DEFAULT_ID}") { authenticated() }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("""{"id":"${Timeline.DEFAULT_ID}"}""", response.bodyAsText())
    }

    @Test
    fun testGetUnknownTimeline() = testApplication {
        val mocks = Mocks()
        mocks.signedIn()
        coEvery { mocks.getTimelineUseCase(any()) } returns null
        configureApp(mocks)

        val response = client.get("/api/v1/timelines/${RoutesTestHelper.TEST_POST_ID}") { authenticated() }

        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("""{"error":"timelines_not_found"}""", response.bodyAsText())
    }

    @Test
    fun testListPosts() = testApplication {
        val mocks = Mocks()
        mocks.signedIn()
        coEvery { mocks.getTimelineUseCase(Timeline.DEFAULT_ID) } returns RoutesTestHelper.testTimeline
        coEvery {
            mocks.getTimelinePostsUseCase(Timeline.DEFAULT_ID, any(), RoutesTestHelper.TEST_USER_ID)
        } returns listOf(RoutesTestHelper.testPost)
        configureApp(mocks)

        val response = client.get("/api/v1/timelines/${Timeline.DEFAULT_ID}/posts") { authenticated() }

        assertEquals(HttpStatusCode.OK, response.status)
        assertContains(response.bodyAsText(), "Hello world!")
    }

}

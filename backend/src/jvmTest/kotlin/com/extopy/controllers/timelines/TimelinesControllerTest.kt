package com.extopy.controllers.timelines

import com.extopy.models.posts.Post
import com.extopy.models.timelines.Timeline
import com.extopy.models.users.User
import com.extopy.models.users.UserContext
import com.extopy.usecases.timelines.IGetTimelineByIdUseCase
import com.extopy.usecases.timelines.IGetTimelinePostsUseCase
import dev.kaccelero.commons.exceptions.ControllerException
import dev.kaccelero.commons.users.IRequireUserForCallUseCase
import dev.kaccelero.models.UUID
import dev.kaccelero.repositories.Pagination
import io.ktor.http.*
import io.ktor.server.application.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TimelinesControllerTest {

    @Test
    fun testGetDefaultTimeline() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getTimelineUseCase = mockk<IGetTimelineByIdUseCase>()
        val call = mockk<ApplicationCall>()
        val user = User(UUID(), "displayName", "username")
        val timeline = Timeline(UUID())
        coEvery { requireUserForCallUseCase(call) } returns user
        coEvery { getTimelineUseCase(timeline.id, UserContext(user.id)) } returns timeline
        val controller = TimelinesController(
            requireUserForCallUseCase,
            getTimelineUseCase,
            mockk()
        )
        assertEquals(timeline, controller.get(call, timeline.id))
    }

    @Test
    fun testGetNotFound() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getTimelineUseCase = mockk<IGetTimelineByIdUseCase>()
        val call = mockk<ApplicationCall>()
        val user = User(UUID(), "displayName", "username")
        coEvery { requireUserForCallUseCase(call) } returns user
        coEvery { getTimelineUseCase(Timeline.defaultId, UserContext(user.id)) } returns null
        val controller = TimelinesController(
            requireUserForCallUseCase,
            getTimelineUseCase,
            mockk()
        )
        val exception = assertFailsWith<ControllerException> {
            controller.get(call, Timeline.defaultId)
        }
        assertEquals(HttpStatusCode.NotFound, exception.code)
        assertEquals("timelines_not_found", exception.key)
    }

    @Test
    fun testGetPosts() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getTimelineUseCase = mockk<IGetTimelineByIdUseCase>()
        val getTimelinePostsUseCase = mockk<IGetTimelinePostsUseCase>()
        val timeline = Timeline(UUID())
        val user = User(UUID(), "displayName", "username")
        val post = Post(UUID(), user.id, body = "body", publishedAt = Clock.System.now())
        coEvery { requireUserForCallUseCase(any()) } returns user
        coEvery { getTimelineUseCase(timeline.id, UserContext(user.id)) } returns timeline
        coEvery { getTimelinePostsUseCase(timeline.id, Pagination(10, 5), UserContext(user.id)) } returns listOf(post)
        val controller = TimelinesController(
            requireUserForCallUseCase,
            getTimelineUseCase,
            getTimelinePostsUseCase
        )
        assertEquals(listOf(post), controller.listPosts(mockk(), timeline.id, 10, 5))
    }

    @Test
    fun testGetPostsNotFound() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getTimelineUseCase = mockk<IGetTimelineByIdUseCase>()
        val user = User(UUID(), "displayName", "username")
        coEvery { requireUserForCallUseCase(any()) } returns user
        coEvery { getTimelineUseCase(Timeline.defaultId, UserContext(user.id)) } returns null
        val controller = TimelinesController(
            requireUserForCallUseCase,
            getTimelineUseCase,
            mockk()
        )
        val exception = assertFailsWith<ControllerException> {
            controller.listPosts(mockk(), Timeline.defaultId, null, null)
        }
        assertEquals(HttpStatusCode.NotFound, exception.code)
        assertEquals("timelines_not_found", exception.key)
    }

    @Test
    fun testGetPostsDefaultLimitOffset() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getTimelineUseCase = mockk<IGetTimelineByIdUseCase>()
        val getTimelinePostsUseCase = mockk<IGetTimelinePostsUseCase>()
        val timeline = Timeline(UUID())
        val user = User(UUID(), "displayName", "username")
        val post = Post(UUID(), user.id, body = "body", publishedAt = Clock.System.now())
        coEvery { requireUserForCallUseCase(any()) } returns user
        coEvery { getTimelineUseCase(timeline.id, UserContext(user.id)) } returns timeline
        coEvery { getTimelinePostsUseCase(timeline.id, Pagination(25, 0), UserContext(user.id)) } returns listOf(post)
        val controller = TimelinesController(
            requireUserForCallUseCase,
            getTimelineUseCase,
            getTimelinePostsUseCase
        )
        assertEquals(listOf(post), controller.listPosts(mockk(), timeline.id, null, null))
    }

}

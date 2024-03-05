package me.nathanfallet.extopy.controllers.timelines

import io.ktor.http.*
import io.ktor.server.application.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.timelines.Timeline
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.extopy.usecases.timelines.IGetTimelineByIdUseCase
import me.nathanfallet.extopy.usecases.timelines.IGetTimelinePostsUseCase
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.ktorx.usecases.users.IRequireUserForCallUseCase
import me.nathanfallet.usecases.pagination.Pagination
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TimelinesControllerTest {

    @Test
    fun testGetDefaultTimeline() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getTimelineUseCase = mockk<IGetTimelineByIdUseCase>()
        val call = mockk<ApplicationCall>()
        val user = User("id", "displayName", "username")
        val timeline = Timeline("default")
        coEvery { requireUserForCallUseCase(call) } returns user
        coEvery { getTimelineUseCase("default", UserContext(user.id)) } returns timeline
        val controller = TimelinesController(
            requireUserForCallUseCase,
            getTimelineUseCase,
            mockk()
        )
        assertEquals(timeline, controller.get(call, "default"))
    }

    @Test
    fun testGetNotFound() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getTimelineUseCase = mockk<IGetTimelineByIdUseCase>()
        val call = mockk<ApplicationCall>()
        val user = User("id", "displayName", "username")
        coEvery { requireUserForCallUseCase(call) } returns user
        coEvery { getTimelineUseCase("default", UserContext(user.id)) } returns null
        val controller = TimelinesController(
            requireUserForCallUseCase,
            getTimelineUseCase,
            mockk()
        )
        val exception = assertFailsWith<ControllerException> {
            controller.get(call, "default")
        }
        assertEquals(HttpStatusCode.NotFound, exception.code)
        assertEquals("timelines_not_found", exception.key)
    }

    @Test
    fun testGetPosts() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getTimelineUseCase = mockk<IGetTimelineByIdUseCase>()
        val getTimelinePostsUseCase = mockk<IGetTimelinePostsUseCase>()
        val timeline = Timeline("default")
        val user = User("id", "displayName", "username")
        val post = Post("postId", "userId", body = "body")
        coEvery { requireUserForCallUseCase(any()) } returns user
        coEvery { getTimelineUseCase("default", UserContext(user.id)) } returns timeline
        coEvery { getTimelinePostsUseCase("default", Pagination(10, 5), UserContext(user.id)) } returns listOf(post)
        val controller = TimelinesController(
            requireUserForCallUseCase,
            getTimelineUseCase,
            getTimelinePostsUseCase
        )
        assertEquals(listOf(post), controller.listPosts(mockk(), "default", 10, 5))
    }

    @Test
    fun testGetPostsNotFound() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getTimelineUseCase = mockk<IGetTimelineByIdUseCase>()
        val user = User("id", "displayName", "username")
        coEvery { requireUserForCallUseCase(any()) } returns user
        coEvery { getTimelineUseCase("default", UserContext(user.id)) } returns null
        val controller = TimelinesController(
            requireUserForCallUseCase,
            getTimelineUseCase,
            mockk()
        )
        val exception = assertFailsWith<ControllerException> {
            controller.listPosts(mockk(), "default", null, null)
        }
        assertEquals(HttpStatusCode.NotFound, exception.code)
        assertEquals("timelines_not_found", exception.key)
    }

    @Test
    fun testGetPostsDefaultLimitOffset() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getTimelineUseCase = mockk<IGetTimelineByIdUseCase>()
        val getTimelinePostsUseCase = mockk<IGetTimelinePostsUseCase>()
        val timeline = Timeline("default")
        val user = User("id", "displayName", "username")
        val post = Post("postId", "userId", body = "body")
        coEvery { requireUserForCallUseCase(any()) } returns user
        coEvery { getTimelineUseCase("default", UserContext(user.id)) } returns timeline
        coEvery { getTimelinePostsUseCase("default", Pagination(25, 0), UserContext(user.id)) } returns listOf(post)
        val controller = TimelinesController(
            requireUserForCallUseCase,
            getTimelineUseCase,
            getTimelinePostsUseCase
        )
        assertEquals(listOf(post), controller.listPosts(mockk(), "default", null, null))
    }

}

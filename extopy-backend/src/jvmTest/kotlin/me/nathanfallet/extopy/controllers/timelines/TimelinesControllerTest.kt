package me.nathanfallet.extopy.controllers.timelines

import io.ktor.http.*
import io.ktor.server.application.*
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.nathanfallet.extopy.models.timelines.Timeline
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.extopy.usecases.timelines.IGetTimelineByIdUseCase
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.ktorx.usecases.users.IRequireUserForCallUseCase
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
        val timeline = Timeline("id")
        coEvery { requireUserForCallUseCase(call) } returns user
        coEvery { getTimelineUseCase("default", 10, 5, UserContext(user.id)) } returns timeline
        every { call.parameters["limit"] } returns "10"
        every { call.parameters["offset"] } returns "5"
        val controller = TimelinesController(
            requireUserForCallUseCase,
            getTimelineUseCase
        )
        assertEquals(timeline, controller.get(call, "default"))
    }

    @Test
    fun testGetDefaultLimitOffsetTimeline() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getTimelineUseCase = mockk<IGetTimelineByIdUseCase>()
        val call = mockk<ApplicationCall>()
        val user = User("id", "displayName", "username")
        val timeline = Timeline("id")
        coEvery { requireUserForCallUseCase(call) } returns user
        coEvery { getTimelineUseCase("default", 25, 0, UserContext(user.id)) } returns timeline
        every { call.parameters["limit"] } returns null
        every { call.parameters["offset"] } returns null
        val controller = TimelinesController(
            requireUserForCallUseCase,
            getTimelineUseCase
        )
        assertEquals(timeline, controller.get(call, "default"))
    }

    @Test
    fun testGetDefaultLimitOffsetInvalidTimeline() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getTimelineUseCase = mockk<IGetTimelineByIdUseCase>()
        val call = mockk<ApplicationCall>()
        val user = User("id", "displayName", "username")
        val timeline = Timeline("id")
        coEvery { requireUserForCallUseCase(call) } returns user
        coEvery { getTimelineUseCase("default", 25, 0, UserContext(user.id)) } returns timeline
        every { call.parameters["limit"] } returns "a"
        every { call.parameters["offset"] } returns "b"
        val controller = TimelinesController(
            requireUserForCallUseCase,
            getTimelineUseCase
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
        coEvery { getTimelineUseCase("default", 10, 5, UserContext(user.id)) } returns null
        every { call.parameters["limit"] } returns "10"
        every { call.parameters["offset"] } returns "5"
        val controller = TimelinesController(
            requireUserForCallUseCase,
            getTimelineUseCase
        )
        val exception = assertFailsWith<ControllerException> {
            controller.get(call, "default")
        }
        assertEquals(HttpStatusCode.NotFound, exception.code)
        assertEquals("timelines_not_found", exception.key)
    }

}

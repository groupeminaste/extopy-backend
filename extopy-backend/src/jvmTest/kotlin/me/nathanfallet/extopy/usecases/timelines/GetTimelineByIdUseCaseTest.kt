package me.nathanfallet.extopy.usecases.timelines

import kotlinx.coroutines.runBlocking
import me.nathanfallet.extopy.models.timelines.Timeline
import me.nathanfallet.extopy.models.users.UserContext
import kotlin.test.Test
import kotlin.test.assertEquals

class GetTimelineByIdUseCaseTest {

    @Test
    fun testGetDefaultTimeline() = runBlocking {
        val useCase = GetTimelineByIdUseCase()
        assertEquals(
            Timeline("default"),
            useCase("default", UserContext("userId"))
        )
    }

    @Test
    fun testGetTrendsTimeline() = runBlocking {
        val useCase = GetTimelineByIdUseCase()
        assertEquals(
            Timeline("trends"),
            useCase("trends", UserContext("userId"))
        )
    }

}

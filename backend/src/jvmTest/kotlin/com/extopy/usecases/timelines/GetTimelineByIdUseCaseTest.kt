package com.extopy.usecases.timelines

import kotlinx.coroutines.runBlocking
import com.extopy.models.timelines.Timeline
import com.extopy.models.users.UserContext
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

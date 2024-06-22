package com.extopy.usecases.timelines

import com.extopy.models.timelines.Timeline
import com.extopy.models.users.UserContext
import dev.kaccelero.models.UUID
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class GetTimelineByIdUseCaseTest {

    @Test
    fun testGetDefaultTimeline() = runBlocking {
        val useCase = GetTimelineByIdUseCase()
        assertEquals(
            Timeline(Timeline.defaultId),
            useCase(Timeline.defaultId, UserContext(UUID()))
        )
    }

}

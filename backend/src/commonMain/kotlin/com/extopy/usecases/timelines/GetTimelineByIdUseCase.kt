package com.extopy.usecases.timelines

import com.extopy.models.timelines.Timeline
import com.extopy.models.users.UserContext
import dev.kaccelero.models.UUID

class GetTimelineByIdUseCase : IGetTimelineByIdUseCase {

    override suspend fun invoke(input1: UUID, input2: UserContext): Timeline? {
        return when (input1) {
            Timeline.defaultId -> Timeline(Timeline.defaultId)
            else -> null // TODO: Custom timelines
        }
    }

}

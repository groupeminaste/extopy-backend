package com.extopy.domain.usecases.timelines

import com.extopy.domain.models.timelines.Timeline
import kotlin.uuid.Uuid

class GetTimelineUseCaseImpl : GetTimelineUseCase {

    // Note: only the default timeline exists for now, custom ones are still to come.
    override suspend fun invoke(id: Uuid): Timeline? =
        Timeline(Timeline.DEFAULT_ID).takeIf { id == Timeline.DEFAULT_ID }

}

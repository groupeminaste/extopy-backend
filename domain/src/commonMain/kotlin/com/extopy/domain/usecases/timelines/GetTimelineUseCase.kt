package com.extopy.domain.usecases.timelines

import com.extopy.domain.models.timelines.Timeline
import kotlin.uuid.Uuid

/**
 * Resolves a timeline from its ID.
 */
interface GetTimelineUseCase {

    suspend operator fun invoke(id: Uuid): Timeline?

}

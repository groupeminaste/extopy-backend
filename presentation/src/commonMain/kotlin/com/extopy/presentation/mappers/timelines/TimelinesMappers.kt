package com.extopy.presentation.mappers.timelines

import com.extopy.api.responses.timelines.TimelineResponse
import com.extopy.domain.models.timelines.Timeline

/**
 * Maps a [Timeline] to a [TimelineResponse].
 */
fun Timeline.toTimelineResponse() = TimelineResponse(
    id = id,
)

package com.extopy.api.responses.timelines

import dev.zodable.Zodable
import kotlinx.serialization.Serializable
import kotlin.js.JsExport
import kotlin.uuid.Uuid

/**
 * A timeline, as the API exposes it.
 */
@Zodable
@JsExport
@Serializable
data class TimelineResponse(
    val id: Uuid,
)

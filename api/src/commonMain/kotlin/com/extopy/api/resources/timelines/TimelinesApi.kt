package com.extopy.api.resources.timelines

import io.ktor.resources.*
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

/**
 * Resource class representing timeline-related API endpoints.
 */
@Serializable
@Resource("/api/v1/timelines")
class TimelinesApi {

    /**
     * Resource class representing a specific timeline identified by its ID.
     */
    @Resource("{timelineId}")
    class Id(
        /**
         * The ID of the timeline.
         */
        val timelineId: Uuid,
        /**
         * The parent TimelinesApi resource.
         */
        val parent: TimelinesApi = TimelinesApi(),
    ) {

        /**
         * Resource class representing the posts of a timeline.
         */
        @Resource("posts")
        class Posts(
            /**
             * The parent timeline resource.
             */
            val parent: Id,
        )

    }

}

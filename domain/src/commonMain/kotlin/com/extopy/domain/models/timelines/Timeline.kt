package com.extopy.domain.models.timelines

import kotlin.uuid.Uuid

/**
 * A timeline of posts.
 *
 * Note: only the default timeline exists for now, custom ones are still to come.
 */
data class Timeline(
    val id: Uuid,
) {

    companion object {

        /**
         * The timeline every account gets, made of the posts of the accounts it follows.
         */
        val DEFAULT_ID: Uuid = Uuid.parse("00000000-0000-0000-0000-000000000000")

    }

}

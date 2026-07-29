package com.extopy.domain.models.users

import kotlin.uuid.Uuid

/**
 * A follow link, from [userId] to [targetId].
 *
 * A link to a personal account starts out not accepted, which is what keeps its posts out of the
 * timeline of the follower until it is.
 */
data class FollowerInUser(
    val userId: Uuid,
    val targetId: Uuid,
    val accepted: Boolean? = null,
    val user: User? = null,
    val target: User? = null,
)

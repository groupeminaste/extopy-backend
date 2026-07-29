package com.extopy.domain.models.posts

import com.extopy.domain.models.users.User
import kotlin.time.Instant
import kotlin.uuid.Uuid

/**
 * A post of Extopy.
 *
 * As for [User], the counters and the `likesIn` flag are only filled in by the queries that join
 * them, so they say "not read" rather than "zero" when null.
 */
data class Post(
    val id: Uuid,
    val userId: Uuid,
    val user: User? = null,
    val repliedToId: Uuid? = null,
    val repostOfId: Uuid? = null,
    val body: String? = null,
    val publishedAt: Instant,
    val editedAt: Instant? = null,
    val expiresAt: Instant? = null,
    val visibility: String? = null,
    val likesCount: Long? = null,
    val repliesCount: Long? = null,
    val repostsCount: Long? = null,
    val likesIn: Boolean? = null,
) {

    companion object {

        /**
         * How long the body of a post may be.
         */
        const val BODY_MAX_LENGTH = 20_000

    }

}

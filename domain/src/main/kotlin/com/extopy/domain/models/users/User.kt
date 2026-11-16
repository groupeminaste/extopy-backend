package com.extopy.domain.models.users

import kotlinx.datetime.LocalDate
import kotlin.time.Instant
import kotlin.uuid.Uuid

/**
 * A user of Extopy.
 *
 * Everything past the identity is nullable: which fields are filled in depends on the query the
 * user was read through, and the counters only exist on the queries that join them in.
 */
data class User(
    val id: Uuid,
    val displayName: String,
    val username: String,
    val email: String? = null,
    val password: String? = null,
    val biography: String? = null,
    val avatar: String? = null,
    val birthdate: LocalDate? = null,
    val joinDate: Instant? = null,
    val lastActive: Instant? = null,
    val personal: Boolean? = null,
    val verified: Boolean? = null,
    val banned: Boolean? = null,
    val postsCount: Long? = null,
    val followersCount: Long? = null,
    val followingCount: Long? = null,
    val followersIn: Boolean? = null,
    val followingIn: Boolean? = null,
) {

    companion object {

        /**
         * What a username may be made of.
         */
        const val USERNAME_REGEX = "[a-zA-Z0-9_]+"

        /**
         * How long a username may be.
         */
        const val USERNAME_MAX_LENGTH = 25

        /**
         * How long a display name may be.
         */
        const val DISPLAY_NAME_MAX_LENGTH = 40

        /**
         * The avatar a new account starts with.
         */
        const val DEFAULT_AVATAR = "https://extopy.com/img/avatar.png"

        /**
         * The biography a new account starts with.
         */
        const val DEFAULT_BIOGRAPHY = "Hello, I'm new on Extopy!"

    }

}

package com.extopy.api.responses.users

import dev.zodable.Zodable
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlin.js.JsExport
import kotlin.time.Instant
import kotlin.uuid.Uuid

/**
 * A user, as the API exposes it.
 *
 * Every field but the identity is nullable: which ones are filled in depends on the query the user
 * was read through, and the serializer leaves the missing ones out of the payload entirely.
 */
@Zodable
@JsExport
@Serializable
data class UserResponse(
    val id: Uuid,
    val displayName: String,
    val username: String,
    val email: String? = null,
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
)

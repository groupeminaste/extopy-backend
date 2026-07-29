package com.extopy.api.resources.users

import io.ktor.resources.*
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

/**
 * Resource class representing the accounts a user follows.
 *
 * Note: the derived routing of the previous framework put this under `followers`, which is not where
 * anything expected it — the client it shipped with was already asking for it here, and getting a
 * 404 for it. It now sits next to `followers`, which is where it reads right and where that client
 * was looking all along.
 */
@Serializable
@Resource("/api/v1/users/{userId}/following")
class FollowingInUsersApi(
    /**
     * The ID of the user whose follows are being listed.
     */
    val userId: Uuid,
)

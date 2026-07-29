package com.extopy.api.resources.users

import io.ktor.resources.*
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

/**
 * Resource class representing the followers of a user.
 */
@Serializable
@Resource("/api/v1/users/{userId}/followers")
class FollowersInUsersApi(
    /**
     * The ID of the user the followers belong to.
     */
    val userId: Uuid,
) {

    /**
     * Resource class representing a specific follower identified by its user ID.
     */
    @Resource("{followerId}")
    class Id(
        /**
         * The ID of the user following the parent user.
         */
        val followerId: Uuid,
        /**
         * The parent FollowersInUsersApi resource.
         */
        val parent: FollowersInUsersApi,
    )

}

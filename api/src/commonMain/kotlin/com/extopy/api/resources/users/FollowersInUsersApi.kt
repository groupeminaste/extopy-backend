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
     * Resource class representing the users a user follows.
     *
     * Note: this sits under `followers` rather than next to it, which is where the derived routing
     * of the previous framework put it. Moving it would break every client already calling it.
     */
    @Resource("following")
    class Following(
        /**
         * The parent FollowersInUsersApi resource.
         */
        val parent: FollowersInUsersApi,
    )

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

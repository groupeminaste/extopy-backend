package com.extopy.api.resources.users

import io.ktor.resources.*
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

/**
 * Resource class representing user-related API endpoints.
 */
@Serializable
@Resource("/api/v1/users")
class UsersApi {

    /**
     * Resource class representing a specific user identified by its ID.
     */
    @Resource("{userId}")
    class Id(
        /**
         * The ID of the user.
         */
        val userId: Uuid,
        /**
         * The parent UsersApi resource.
         */
        val parent: UsersApi = UsersApi(),
    ) {

        /**
         * Resource class representing the posts of a user.
         */
        @Resource("posts")
        class Posts(
            /**
             * The parent user resource.
             */
            val parent: Id,
        )

    }

}

package com.extopy.api.resources.posts

import io.ktor.resources.*
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

/**
 * Resource class representing post-related API endpoints.
 */
@Serializable
@Resource("/api/v1/posts")
class PostsApi {

    /**
     * Resource class representing a specific post identified by its ID.
     */
    @Resource("{postId}")
    class Id(
        /**
         * The ID of the post.
         */
        val postId: Uuid,
        /**
         * The parent PostsApi resource.
         */
        val parent: PostsApi = PostsApi(),
    ) {

        /**
         * Resource class representing the replies to a post.
         */
        @Resource("replies")
        class Replies(
            /**
             * The parent post resource.
             */
            val parent: Id,
        )

    }

}

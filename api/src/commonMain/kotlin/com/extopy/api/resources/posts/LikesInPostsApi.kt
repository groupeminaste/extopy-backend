package com.extopy.api.resources.posts

import io.ktor.resources.*
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

/**
 * Resource class representing the likes of a post.
 */
@Serializable
@Resource("/api/v1/posts/{postId}/likes")
class LikesInPostsApi(
    /**
     * The ID of the post the likes belong to.
     */
    val postId: Uuid,
) {

    /**
     * Resource class representing the like of a specific user.
     */
    @Resource("{userId}")
    class Id(
        /**
         * The ID of the user who liked the post.
         */
        val userId: Uuid,
        /**
         * The parent LikesInPostsApi resource.
         */
        val parent: LikesInPostsApi,
    )

}

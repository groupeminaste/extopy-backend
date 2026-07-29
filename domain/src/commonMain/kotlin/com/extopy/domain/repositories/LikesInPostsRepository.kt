package com.extopy.domain.repositories

import com.extopy.domain.models.application.Pagination
import com.extopy.domain.models.posts.LikeInPost
import kotlin.uuid.Uuid

/**
 * Reads and writes the likes on the posts.
 */
interface LikesInPostsRepository {

    /**
     * Lists the accounts that liked a post.
     */
    suspend fun list(pagination: Pagination, postId: Uuid): List<LikeInPost>

    /**
     * Likes a post.
     */
    suspend fun create(postId: Uuid, userId: Uuid): LikeInPost?

    /**
     * Removes the like of an account on a post.
     *
     * @return True if a row was deleted.
     */
    suspend fun delete(postId: Uuid, userId: Uuid): Boolean

}

package com.extopy.domain.repositories

import com.extopy.domain.models.application.Pagination
import com.extopy.domain.models.posts.Post
import com.extopy.domain.models.posts.PostPayload
import kotlin.uuid.Uuid

/**
 * Reads and writes the posts.
 *
 * Every read takes the user doing it, because the counters carry whether that user liked the post.
 */
interface PostsRepository {

    /**
     * Lists every post, newest first.
     */
    suspend fun list(pagination: Pagination, viewedBy: Uuid): List<Post>

    /**
     * Lists the posts of the accounts a user follows, plus its own, newest first.
     */
    suspend fun listDefault(pagination: Pagination, viewedBy: Uuid): List<Post>

    /**
     * Lists the posts with the most likes, replies and reposts first.
     */
    suspend fun listTrends(pagination: Pagination, viewedBy: Uuid): List<Post>

    /**
     * Lists the posts of an account, newest first.
     */
    suspend fun listUserPosts(userId: Uuid, pagination: Pagination, viewedBy: Uuid): List<Post>

    /**
     * Lists the replies to a post, newest first.
     */
    suspend fun listReplies(postId: Uuid, pagination: Pagination, viewedBy: Uuid): List<Post>

    /**
     * Reads a post.
     */
    suspend fun get(id: Uuid, viewedBy: Uuid): Post?

    /**
     * Publishes a post.
     *
     * @param payload What to publish.
     * @param userId The account publishing it.
     */
    suspend fun create(payload: PostPayload, userId: Uuid): Post?

    /**
     * Rewrites the body of a post, and stamps it as edited.
     *
     * @return True if a row was written.
     */
    suspend fun update(id: Uuid, payload: PostPayload): Boolean

    /**
     * Deletes a post.
     *
     * @return True if a row was deleted.
     */
    suspend fun delete(id: Uuid): Boolean

}

package com.extopy.infrastructure.database.tables

import com.extopy.domain.models.posts.LikeInPost
import com.extopy.domain.models.posts.Post
import com.extopy.domain.models.users.User
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.alias

object LikesInPosts : Table() {

    val postId = uuid("post_id").index()
    val userId = uuid("user_id").index()

    /**
     * The same table joined a second time, to tell whether the account doing the reading is one of
     * the accounts that liked the post.
     */
    val likesIn = LikesInPosts.alias("PostsLikesIn")

    override val primaryKey = PrimaryKey(arrayOf(postId, userId))

    fun toLikeInPost(
        row: ResultRow,
        post: Post? = null,
        user: User? = null,
    ) = LikeInPost(
        row[postId],
        row[userId],
        post,
        user
    )

}

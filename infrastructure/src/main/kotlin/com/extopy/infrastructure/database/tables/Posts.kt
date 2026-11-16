package com.extopy.infrastructure.database.tables

import com.extopy.domain.models.posts.Post
import com.extopy.domain.models.users.User
import org.jetbrains.exposed.v1.core.LongColumnType
import org.jetbrains.exposed.v1.core.PlusOp
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.alias
import org.jetbrains.exposed.v1.core.countDistinct
import org.jetbrains.exposed.v1.core.dao.id.UuidTable
import org.jetbrains.exposed.v1.datetime.timestamp

object Posts : UuidTable() {

    val userId = uuid("user_id").index()
    val repliedToId = uuid("replied_to_id").nullable()
    val repostOfId = uuid("repost_of_id").nullable()
    val body = text("body")
    val publishedAt = timestamp("published_at")
    val editedAt = timestamp("edited_at").nullable().default(null)
    val expiresAt = timestamp("expires_at")
    val visibility = text("visibility")

    /**
     * The same table joined again, to count what points back at a post.
     */
    val replies = Posts.alias("PostsReplies")
    val reposts = Posts.alias("PostsReposts")

    val likesCount = LikesInPosts.userId.countDistinct()
    val repliesCount = replies[id].countDistinct()
    val repostsCount = reposts[id].countDistinct()
    val likesIn = LikesInPosts.likesIn[LikesInPosts.userId].countDistinct()

    /**
     * What the trending timeline orders by: everything a post got, added up.
     */
    val trendsCount = PlusOp(
        likesCount,
        PlusOp(repliesCount, repostsCount, LongColumnType()),
        LongColumnType()
    )

    fun toPost(
        row: ResultRow,
        user: User? = null,
    ) = Post(
        row[id].value,
        row[userId],
        user,
        row.getOrNull(repliedToId),
        row.getOrNull(repostOfId),
        row.getOrNull(body),
        row[publishedAt],
        row.getOrNull(editedAt),
        row.getOrNull(expiresAt),
        row.getOrNull(visibility),
        row.getOrNull(likesCount),
        row.getOrNull(repliesCount),
        row.getOrNull(repostsCount),
        row.getOrNull(likesIn)?.let { it >= 1L }
    )

}

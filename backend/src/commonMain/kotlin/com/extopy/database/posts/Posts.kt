package com.extopy.database.posts

import com.extopy.models.posts.Post
import com.extopy.models.users.User
import dev.kaccelero.models.UUID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object Posts : UUIDTable() {

    val userId = uuid("user_id").index()
    val repliedToId = uuid("replied_to_id").nullable()
    val repostOfId = uuid("repost_of_id").nullable()
    val body = text("body")
    val publishedAt = timestamp("published_at")
    val editedAt = timestamp("edited_at").nullable().default(null)
    val expiresAt = timestamp("expires_at")
    val visibility = text("visibility")

    val replies = Posts.alias("PostsReplies")
    val reposts = Posts.alias("PostsReposts")

    val likesCount = LikesInPosts.userId.countDistinct()
    val repliesCount = replies[id].countDistinct()
    val repostsCount = reposts[id].countDistinct()
    val likesIn = LikesInPosts.likesIn[LikesInPosts.userId].countDistinct()
    val trendsCount = PlusOp(
        likesCount,
        PlusOp(repliesCount, repostsCount, LongColumnType()),
        LongColumnType()
    )

    fun toPost(
        row: ResultRow,
        user: User? = null,
    ) = Post(
        UUID(row[id].value),
        UUID(row[userId]),
        user,
        row.getOrNull(repliedToId)?.let(::UUID),
        row.getOrNull(repostOfId)?.let(::UUID),
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

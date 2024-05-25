package me.nathanfallet.extopy.database.posts

import kotlinx.datetime.toInstant
import me.nathanfallet.extopy.extensions.generateId
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.users.User
import org.jetbrains.exposed.sql.*

object Posts : Table() {

    val id = varchar("id", 32)
    val userId = varchar("user_id", 32).index()
    val repliedToId = varchar("replied_to_id", 32).nullable()
    val repostOfId = varchar("repost_of_id", 32).nullable()
    val body = text("body")
    val published = varchar("published", 255)
    val edited = varchar("edited", 255).nullable()
    val expiration = varchar("expiration", 255)
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

    override val primaryKey = PrimaryKey(id)

    fun generateId(): String {
        val candidate = String.generateId()
        return if (selectAll().where { id eq candidate }.count() > 0) generateId() else candidate
    }

    fun toPost(
        row: ResultRow,
        user: User? = null,
    ) = Post(
        row[id],
        row.getOrNull(userId),
        user,
        row.getOrNull(repliedToId),
        row.getOrNull(repostOfId),
        row.getOrNull(body),
        row.getOrNull(published)?.toInstant(),
        row.getOrNull(edited)?.toInstant(),
        row.getOrNull(expiration)?.toInstant(),
        row.getOrNull(visibility),
        row.getOrNull(likesCount),
        row.getOrNull(repliesCount),
        row.getOrNull(repostsCount),
        row.getOrNull(likesIn)?.let { it >= 1L }
    )

}

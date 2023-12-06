package me.nathanfallet.extopy.database.posts

import kotlinx.datetime.toInstant
import me.nathanfallet.extopy.database.users.Users
import me.nathanfallet.extopy.extensions.generateId
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.users.User
import org.jetbrains.exposed.sql.*

object Posts : Table() {

    val id = varchar("id", 32)
    val userId = varchar("user_id", 32)
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
        PlusOp(repliesCount, repostsCount, IntegerColumnType()),
        IntegerColumnType()
    )

    override val primaryKey = PrimaryKey(id)

    fun generateId(): String {
        val candidate = String.generateId()
        return if (select { id eq candidate }.count() > 0) generateId() else candidate
    }

    fun toPost(
        row: ResultRow,
        user: User?,
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

    fun delete(id: String) {
        LikesInPosts.deleteWhere {
            Op.build { postId eq id }
        }
        Posts.deleteWhere {
            Op.build { Posts.id eq id }
        }
        Posts.select {
            repliedToId eq id or (repostOfId eq id)
        }.forEach {
            delete(it[Posts.id])
        }
    }

    fun customJoinnable(viewedBy: String): ColumnSet {
        return Posts.join(Users, JoinType.INNER, userId, Users.id)
            .join(LikesInPosts, JoinType.LEFT, id, LikesInPosts.postId)
            .join(replies, JoinType.LEFT, id, replies[repliedToId])
            .join(reposts, JoinType.LEFT, id, reposts[repostOfId])
            .join(
                LikesInPosts.likesIn,
                JoinType.LEFT,
                id,
                LikesInPosts.likesIn[LikesInPosts.postId],
                { LikesInPosts.likesIn[LikesInPosts.userId] eq viewedBy }
            )
    }

    fun customJoin(viewedBy: String): FieldSet {
        return customJoinnable(viewedBy).customPostsSlice()
    }
}

fun ColumnSet.customPostsSlice(additionalFields: List<Expression<*>> = listOf()): FieldSet {
    return slice(
        Posts.columns +
                Users.id +
                Users.displayName +
                Users.username +
                Users.avatar +
                Users.verified +
                Posts.likesCount +
                Posts.repliesCount +
                Posts.repostsCount +
                Posts.likesIn +
                additionalFields
    )
}

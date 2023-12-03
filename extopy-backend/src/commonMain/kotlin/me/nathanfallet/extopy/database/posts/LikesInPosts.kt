package me.nathanfallet.extopy.database.posts

import me.nathanfallet.extopy.database.users.Users
import me.nathanfallet.extopy.models.posts.LikeInPost
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.users.User
import org.jetbrains.exposed.sql.*

object LikesInPosts : Table() {

    val postId = varchar("post_id", 32)
    val userId = varchar("user_id", 32)

    val likesIn = LikesInPosts.alias("PostsLikesIn")

    override val primaryKey = PrimaryKey(arrayOf(postId, userId))

    fun toLikeInPost(
        row: ResultRow,
        post: Post?,
        user: User?,
    ) = LikeInPost(
        row[postId],
        row[userId],
        post,
        user
    )

    fun customJoin(additionalFields: List<Expression<*>> = listOf()): FieldSet {
        return LikesInPosts
            .join(Users, JoinType.LEFT, userId, Users.id)
            .slice(
                additionalFields +
                        postId +
                        userId +
                        Users.id +
                        Users.displayName +
                        Users.username +
                        Users.avatar +
                        Users.verified
            )
    }

}

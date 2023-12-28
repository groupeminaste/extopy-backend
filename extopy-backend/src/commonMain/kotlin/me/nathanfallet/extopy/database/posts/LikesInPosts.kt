package me.nathanfallet.extopy.database.posts

import me.nathanfallet.extopy.models.posts.LikeInPost
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.users.User
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.alias

object LikesInPosts : Table() {

    val postId = varchar("post_id", 32).index()
    val userId = varchar("user_id", 32).index()

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

}

package com.extopy.database.posts

import com.extopy.models.posts.LikeInPost
import com.extopy.models.posts.Post
import com.extopy.models.users.User
import dev.kaccelero.models.UUID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.alias

object LikesInPosts : Table() {

    val postId = uuid("post_id").index()
    val userId = uuid("user_id").index()

    val likesIn = LikesInPosts.alias("PostsLikesIn")

    override val primaryKey = PrimaryKey(arrayOf(postId, userId))

    fun toLikeInPost(
        row: ResultRow,
        post: Post?,
        user: User?,
    ) = LikeInPost(
        UUID(row[postId]),
        UUID(row[userId]),
        post,
        user
    )

}

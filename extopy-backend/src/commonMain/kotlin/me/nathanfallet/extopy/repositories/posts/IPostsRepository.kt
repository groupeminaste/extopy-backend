package me.nathanfallet.extopy.repositories.posts

import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.posts.PostPayload
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.usecases.models.repositories.IModelSuspendRepository

interface IPostsRepository : IModelSuspendRepository<Post, String, PostPayload, PostPayload> {

    suspend fun listDefault(limit: Long, offset: Long, context: UserContext): List<Post>
    suspend fun listTrends(limit: Long, offset: Long, context: UserContext): List<Post>
    suspend fun listUserPosts(userId: String, limit: Long, offset: Long, context: UserContext): List<Post>
    suspend fun listReplies(postId: String, limit: Long, offset: Long, context: UserContext): List<Post>

}

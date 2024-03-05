package me.nathanfallet.extopy.repositories.posts

import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.posts.PostPayload
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.usecases.models.repositories.IModelSuspendRepository
import me.nathanfallet.usecases.pagination.Pagination

interface IPostsRepository : IModelSuspendRepository<Post, String, PostPayload, PostPayload> {

    suspend fun listDefault(pagination: Pagination, context: UserContext): List<Post>
    suspend fun listTrends(pagination: Pagination, context: UserContext): List<Post>
    suspend fun listUserPosts(userId: String, pagination: Pagination, context: UserContext): List<Post>
    suspend fun listReplies(postId: String, pagination: Pagination, context: UserContext): List<Post>

}

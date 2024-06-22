package com.extopy.repositories.posts

import com.extopy.models.posts.Post
import com.extopy.models.posts.PostPayload
import com.extopy.models.users.UserContext
import dev.kaccelero.repositories.IModelSuspendRepository
import dev.kaccelero.repositories.Pagination

interface IPostsRepository : IModelSuspendRepository<Post, String, PostPayload, PostPayload> {

    suspend fun listDefault(pagination: Pagination, context: UserContext): List<Post>
    suspend fun listTrends(pagination: Pagination, context: UserContext): List<Post>
    suspend fun listUserPosts(userId: String, pagination: Pagination, context: UserContext): List<Post>
    suspend fun listReplies(postId: String, pagination: Pagination, context: UserContext): List<Post>

}

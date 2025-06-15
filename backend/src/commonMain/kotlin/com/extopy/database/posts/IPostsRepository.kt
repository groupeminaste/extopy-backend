package com.extopy.database.posts

import com.extopy.models.posts.Post
import com.extopy.models.posts.PostPayload
import com.extopy.models.users.UserContext
import dev.kaccelero.models.UUID
import dev.kaccelero.repositories.IModelSuspendRepository
import dev.kaccelero.repositories.Pagination

interface IPostsRepository : IModelSuspendRepository<Post, UUID, PostPayload, PostPayload> {

    suspend fun listDefault(pagination: Pagination, context: UserContext): List<Post>
    suspend fun listTrends(pagination: Pagination, context: UserContext): List<Post>
    suspend fun listUserPosts(userId: UUID, pagination: Pagination, context: UserContext): List<Post>
    suspend fun listReplies(postId: UUID, pagination: Pagination, context: UserContext): List<Post>

}

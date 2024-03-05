package me.nathanfallet.extopy.repositories.posts

import me.nathanfallet.extopy.models.posts.LikeInPost
import me.nathanfallet.usecases.pagination.Pagination

interface ILikesInPostsRemoteRepository {

    suspend fun list(pagination: Pagination, postId: String): List<LikeInPost>
    suspend fun create(postId: String): LikeInPost?
    suspend fun delete(postId: String, userId: String): Boolean

}

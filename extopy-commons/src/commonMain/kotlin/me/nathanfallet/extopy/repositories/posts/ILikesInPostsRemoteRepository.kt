package me.nathanfallet.extopy.repositories.posts

import me.nathanfallet.extopy.models.posts.LikeInPost

interface ILikesInPostsRemoteRepository {

    suspend fun list(postId: String): List<LikeInPost>
    suspend fun create(postId: String): LikeInPost?
    suspend fun delete(postId: String, userId: String): Boolean

}

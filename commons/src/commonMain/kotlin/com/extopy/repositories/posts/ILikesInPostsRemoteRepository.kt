package com.extopy.repositories.posts

import com.extopy.models.posts.LikeInPost
import dev.kaccelero.repositories.Pagination

interface ILikesInPostsRemoteRepository {

    suspend fun list(pagination: Pagination, postId: String): List<LikeInPost>
    suspend fun create(postId: String): LikeInPost?
    suspend fun delete(postId: String, userId: String): Boolean

}

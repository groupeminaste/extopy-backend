package com.extopy.repositories.posts

import com.extopy.models.posts.LikeInPost
import dev.kaccelero.models.UUID
import dev.kaccelero.repositories.Pagination

interface ILikesInPostsRemoteRepository {

    suspend fun list(pagination: Pagination, postId: UUID): List<LikeInPost>
    suspend fun create(postId: UUID): LikeInPost?
    suspend fun delete(postId: UUID, userId: UUID): Boolean

}

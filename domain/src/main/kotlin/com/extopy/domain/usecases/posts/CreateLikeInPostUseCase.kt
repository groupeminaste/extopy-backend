package com.extopy.domain.usecases.posts

import com.extopy.domain.models.posts.LikeInPost
import kotlin.uuid.Uuid

/**
 * Likes a post.
 */
interface CreateLikeInPostUseCase {

    suspend operator fun invoke(postId: Uuid, userId: Uuid): LikeInPost?

}

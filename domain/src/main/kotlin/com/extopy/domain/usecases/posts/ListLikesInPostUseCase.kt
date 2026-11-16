package com.extopy.domain.usecases.posts

import com.extopy.domain.models.application.Pagination
import com.extopy.domain.models.posts.LikeInPost
import kotlin.uuid.Uuid

/**
 * Lists the accounts that liked a post.
 */
interface ListLikesInPostUseCase {

    suspend operator fun invoke(pagination: Pagination, postId: Uuid): List<LikeInPost>

}

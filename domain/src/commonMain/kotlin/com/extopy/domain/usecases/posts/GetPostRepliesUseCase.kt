package com.extopy.domain.usecases.posts

import com.extopy.domain.models.application.Pagination
import com.extopy.domain.models.posts.Post
import kotlin.uuid.Uuid

/**
 * Lists the replies to a post, newest first.
 */
interface GetPostRepliesUseCase {

    suspend operator fun invoke(postId: Uuid, pagination: Pagination, viewedBy: Uuid): List<Post>

}

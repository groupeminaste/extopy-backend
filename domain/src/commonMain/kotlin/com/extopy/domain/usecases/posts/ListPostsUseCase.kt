package com.extopy.domain.usecases.posts

import com.extopy.domain.models.application.Pagination
import com.extopy.domain.models.posts.Post
import kotlin.uuid.Uuid

/**
 * Lists the posts, newest first.
 */
interface ListPostsUseCase {

    suspend operator fun invoke(pagination: Pagination, viewedBy: Uuid): List<Post>

}

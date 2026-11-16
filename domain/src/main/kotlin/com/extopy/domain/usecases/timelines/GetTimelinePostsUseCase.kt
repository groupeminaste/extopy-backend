package com.extopy.domain.usecases.timelines

import com.extopy.domain.models.application.Pagination
import com.extopy.domain.models.posts.Post
import kotlin.uuid.Uuid

/**
 * Lists the posts of a timeline.
 */
interface GetTimelinePostsUseCase {

    suspend operator fun invoke(id: Uuid, pagination: Pagination, viewedBy: Uuid): List<Post>

}

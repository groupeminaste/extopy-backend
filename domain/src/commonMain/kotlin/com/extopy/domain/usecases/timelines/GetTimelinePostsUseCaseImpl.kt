package com.extopy.domain.usecases.timelines

import com.extopy.domain.models.application.Pagination
import com.extopy.domain.models.posts.Post
import com.extopy.domain.models.timelines.Timeline
import com.extopy.domain.repositories.PostsRepository
import kotlin.uuid.Uuid

class GetTimelinePostsUseCaseImpl(
    private val repository: PostsRepository,
) : GetTimelinePostsUseCase {

    override suspend fun invoke(id: Uuid, pagination: Pagination, viewedBy: Uuid): List<Post> = when (id) {
        // An account that follows nobody yet would otherwise land on an empty page, so it gets the
        // trends instead until it has something of its own to read.
        Timeline.DEFAULT_ID -> repository.listDefault(pagination, viewedBy).takeIf { it.isNotEmpty() }
            ?: repository.listTrends(pagination, viewedBy)

        else -> emptyList() // Custom timelines are still to come.
    }

}

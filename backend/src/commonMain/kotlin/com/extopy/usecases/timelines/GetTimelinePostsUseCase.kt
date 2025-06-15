package com.extopy.usecases.timelines

import com.extopy.models.posts.Post
import com.extopy.models.timelines.Timeline
import com.extopy.models.users.UserContext
import com.extopy.database.posts.IPostsRepository
import dev.kaccelero.models.UUID
import dev.kaccelero.repositories.Pagination

class GetTimelinePostsUseCase(
    private val postsRepository: IPostsRepository,
) : IGetTimelinePostsUseCase {

    override suspend fun invoke(input1: UUID, input2: Pagination, input3: UserContext): List<Post> =
        when (input1) {
            Timeline.defaultId -> postsRepository.listDefault(input2, input3).takeIf { it.isNotEmpty() }
                ?: postsRepository.listTrends(input2, input3)

            else -> emptyList() // TODO: Custom timelines
        }

}

package com.extopy.usecases.timelines

import com.extopy.models.posts.Post
import com.extopy.models.users.UserContext
import com.extopy.repositories.posts.IPostsRepository
import dev.kaccelero.repositories.Pagination

class GetTimelinePostsUseCase(
    private val postsRepository: IPostsRepository,
) : IGetTimelinePostsUseCase {

    override suspend fun invoke(input1: String, input2: Pagination, input3: UserContext): List<Post> {
        return when (input1) {
            "default" -> postsRepository.listDefault(input2, input3)
            "trends" -> postsRepository.listTrends(input2, input3)
            else -> emptyList() // TODO: Custom timelines
        }
    }

}

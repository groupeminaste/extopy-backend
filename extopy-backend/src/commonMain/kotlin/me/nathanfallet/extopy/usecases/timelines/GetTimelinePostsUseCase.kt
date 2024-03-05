package me.nathanfallet.extopy.usecases.timelines

import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.extopy.repositories.posts.IPostsRepository
import me.nathanfallet.usecases.pagination.Pagination

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

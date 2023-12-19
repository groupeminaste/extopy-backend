package me.nathanfallet.extopy.usecases.timelines

import me.nathanfallet.extopy.models.timelines.Timeline
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.extopy.repositories.posts.IPostsRepository

class GetTimelineByIdUseCase(
    private val postsRepository: IPostsRepository,
) : IGetTimelineByIdUseCase {

    override suspend fun invoke(input1: String, input2: UserContext, input3: Long, input4: Long): Timeline? {
        return when (input1) {
            "default" -> Timeline(
                "default",
                "default",
                posts = postsRepository.listDefault(input3, input4, input2)
            )

            "trends" -> Timeline(
                "trends",
                "trends",
                posts = postsRepository.listTrends(input3, input4, input2)
            )

            else -> null // TODO: Custom timelines
        }
    }

}

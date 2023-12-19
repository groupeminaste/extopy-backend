package me.nathanfallet.extopy.usecases.timelines

import me.nathanfallet.extopy.models.timelines.Timeline
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.extopy.repositories.posts.IPostsRepository

class GetTimelineByIdUseCase(
    private val postsRepository: IPostsRepository,
) : IGetTimelineByIdUseCase {

    override suspend fun invoke(input1: String, input2: Long, input3: Long, input4: UserContext): Timeline? {
        return when (input1) {
            "default" -> Timeline(
                "default",
                posts = postsRepository.listDefault(input2, input3, input4)
            )

            "trends" -> Timeline(
                "trends",
                posts = postsRepository.listTrends(input2, input3, input4)
            )

            else -> null // TODO: Custom timelines
        }
    }

}

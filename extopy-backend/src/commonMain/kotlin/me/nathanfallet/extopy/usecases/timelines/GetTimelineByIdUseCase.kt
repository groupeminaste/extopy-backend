package me.nathanfallet.extopy.usecases.timelines

import me.nathanfallet.extopy.models.timelines.Timeline
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.extopy.repositories.posts.IPostsRepository
import me.nathanfallet.extopy.repositories.users.IUsersRepository

class GetTimelineByIdUseCase(
    private val usersRepository: IUsersRepository,
    private val postsRepository: IPostsRepository,
) : IGetTimelineByIdUseCase {

    override suspend fun invoke(input1: String, input2: UserContext, input3: Long, input4: Long): Timeline? {
        val (type, id) = input1.split(":").let {
            if (it.size >= 2) it[0] to it[1] else it[0] to null
        }
        return when (type) {
            "default" -> Timeline(
                "default",
                "default",
                posts = postsRepository.list(input3, input4, input2)
            )

            else -> null
        }
    }

}

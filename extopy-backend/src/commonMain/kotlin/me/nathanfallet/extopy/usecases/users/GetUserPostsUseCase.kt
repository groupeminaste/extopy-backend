package me.nathanfallet.extopy.usecases.users

import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.extopy.repositories.posts.IPostsRepository
import me.nathanfallet.usecases.pagination.Pagination

class GetUserPostsUseCase(
    private val postsRepository: IPostsRepository,
) : IGetUserPostsUseCase {

    override suspend fun invoke(input1: String, input2: Pagination, input3: UserContext): List<Post> =
        postsRepository.listUserPosts(input1, input2, input3)

}

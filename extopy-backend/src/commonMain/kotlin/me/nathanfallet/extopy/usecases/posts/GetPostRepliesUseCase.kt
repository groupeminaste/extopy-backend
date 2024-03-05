package me.nathanfallet.extopy.usecases.posts

import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.extopy.repositories.posts.IPostsRepository
import me.nathanfallet.usecases.pagination.Pagination

class GetPostRepliesUseCase(
    private val postsRepository: IPostsRepository,
) : IGetPostRepliesUseCase {

    override suspend fun invoke(input1: String, input2: Pagination, input3: UserContext): List<Post> =
        postsRepository.listReplies(input1, input2, input3)

}

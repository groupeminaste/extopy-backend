package com.extopy.usecases.posts

import com.extopy.models.posts.Post
import com.extopy.models.users.UserContext
import com.extopy.repositories.posts.IPostsRepository
import dev.kaccelero.repositories.Pagination

class GetPostRepliesUseCase(
    private val postsRepository: IPostsRepository,
) : IGetPostRepliesUseCase {

    override suspend fun invoke(input1: String, input2: Pagination, input3: UserContext): List<Post> =
        postsRepository.listReplies(input1, input2, input3)

}

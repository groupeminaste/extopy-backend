package com.extopy.usecases.posts

import com.extopy.models.posts.Post
import com.extopy.models.users.UserContext
import com.extopy.database.posts.IPostsRepository
import dev.kaccelero.models.UUID
import dev.kaccelero.repositories.Pagination

class GetPostRepliesUseCase(
    private val postsRepository: IPostsRepository,
) : IGetPostRepliesUseCase {

    override suspend fun invoke(input1: UUID, input2: Pagination, input3: UserContext): List<Post> =
        postsRepository.listReplies(input1, input2, input3)

}

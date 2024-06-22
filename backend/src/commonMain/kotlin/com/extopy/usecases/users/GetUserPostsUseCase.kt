package com.extopy.usecases.users

import com.extopy.models.posts.Post
import com.extopy.models.users.UserContext
import com.extopy.repositories.posts.IPostsRepository
import dev.kaccelero.models.UUID
import dev.kaccelero.repositories.Pagination

class GetUserPostsUseCase(
    private val postsRepository: IPostsRepository,
) : IGetUserPostsUseCase {

    override suspend fun invoke(input1: UUID, input2: Pagination, input3: UserContext): List<Post> =
        postsRepository.listUserPosts(input1, input2, input3)

}

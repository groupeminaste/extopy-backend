package com.extopy.domain.usecases.users

import com.extopy.domain.models.application.Pagination
import com.extopy.domain.models.posts.Post
import com.extopy.domain.repositories.PostsRepository
import kotlin.uuid.Uuid

class GetUserPostsUseCaseImpl(
    private val repository: PostsRepository,
) : GetUserPostsUseCase {

    override suspend fun invoke(userId: Uuid, pagination: Pagination, viewedBy: Uuid): List<Post> =
        repository.listUserPosts(userId, pagination, viewedBy)

}

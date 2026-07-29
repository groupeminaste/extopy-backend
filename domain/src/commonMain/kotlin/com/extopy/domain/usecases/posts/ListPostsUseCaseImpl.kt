package com.extopy.domain.usecases.posts

import com.extopy.domain.models.application.Pagination
import com.extopy.domain.models.posts.Post
import com.extopy.domain.repositories.PostsRepository
import kotlin.uuid.Uuid

class ListPostsUseCaseImpl(
    private val repository: PostsRepository,
) : ListPostsUseCase {

    override suspend fun invoke(pagination: Pagination, viewedBy: Uuid): List<Post> =
        repository.list(pagination, viewedBy)

}

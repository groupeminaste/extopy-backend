package com.extopy.domain.usecases.posts

import com.extopy.domain.models.posts.Post
import com.extopy.domain.repositories.PostsRepository
import kotlin.uuid.Uuid

class GetPostUseCaseImpl(
    private val repository: PostsRepository,
) : GetPostUseCase {

    override suspend fun invoke(id: Uuid, viewedBy: Uuid): Post? = repository.get(id, viewedBy)

}

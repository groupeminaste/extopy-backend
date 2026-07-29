package com.extopy.domain.usecases.posts

import com.extopy.domain.repositories.LikesInPostsRepository
import kotlin.uuid.Uuid

class DeleteLikeInPostUseCaseImpl(
    private val repository: LikesInPostsRepository,
) : DeleteLikeInPostUseCase {

    override suspend fun invoke(postId: Uuid, userId: Uuid): Boolean = repository.delete(postId, userId)

}

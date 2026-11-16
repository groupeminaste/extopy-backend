package com.extopy.domain.usecases.posts

import com.extopy.domain.models.posts.LikeInPost
import com.extopy.domain.repositories.LikesInPostsRepository
import kotlin.uuid.Uuid

class CreateLikeInPostUseCaseImpl(
    private val repository: LikesInPostsRepository,
) : CreateLikeInPostUseCase {

    override suspend fun invoke(postId: Uuid, userId: Uuid): LikeInPost? = repository.create(postId, userId)

}

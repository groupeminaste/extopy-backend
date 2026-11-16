package com.extopy.domain.usecases.posts

import com.extopy.domain.models.application.Pagination
import com.extopy.domain.models.posts.LikeInPost
import com.extopy.domain.repositories.LikesInPostsRepository
import kotlin.uuid.Uuid

class ListLikesInPostUseCaseImpl(
    private val repository: LikesInPostsRepository,
) : ListLikesInPostUseCase {

    override suspend fun invoke(pagination: Pagination, postId: Uuid): List<LikeInPost> =
        repository.list(pagination, postId)

}

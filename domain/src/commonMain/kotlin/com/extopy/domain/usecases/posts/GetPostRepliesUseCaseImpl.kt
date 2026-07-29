package com.extopy.domain.usecases.posts

import com.extopy.domain.models.application.Pagination
import com.extopy.domain.models.posts.Post
import com.extopy.domain.repositories.PostsRepository
import kotlin.uuid.Uuid

class GetPostRepliesUseCaseImpl(
    private val repository: PostsRepository,
) : GetPostRepliesUseCase {

    override suspend fun invoke(postId: Uuid, pagination: Pagination, viewedBy: Uuid): List<Post> =
        repository.listReplies(postId, pagination, viewedBy)

}

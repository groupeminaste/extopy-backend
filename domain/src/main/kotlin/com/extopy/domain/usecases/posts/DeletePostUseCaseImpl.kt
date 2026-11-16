package com.extopy.domain.usecases.posts

import com.extopy.domain.repositories.PostsRepository
import kotlin.uuid.Uuid

class DeletePostUseCaseImpl(
    private val repository: PostsRepository,
) : DeletePostUseCase {

    // Note: the likes, the replies and the reposts of a deleted post are left behind, as they
    // already were before this migration.
    override suspend fun invoke(id: Uuid): Boolean = repository.delete(id)

}

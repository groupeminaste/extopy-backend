package com.extopy.domain.usecases.posts

import com.extopy.domain.exceptions.posts.PostBodyEmptyException
import com.extopy.domain.exceptions.validation.ValidationException
import com.extopy.domain.models.posts.Post
import com.extopy.domain.models.posts.PostPayload
import com.extopy.domain.repositories.PostsRepository
import kotlin.uuid.Uuid

class UpdatePostUseCaseImpl(
    private val repository: PostsRepository,
) : UpdatePostUseCase {

    override suspend fun invoke(id: Uuid, payload: PostPayload, viewedBy: Uuid): Post? {
        if (payload.body.isBlank()) throw PostBodyEmptyException()
        if (payload.body.length > Post.BODY_MAX_LENGTH) throw ValidationException("posts_body_maxLength")
        if (!repository.update(id, payload)) return null
        // Reading it back has to carry the viewer, otherwise the query returns nothing and a write
        // that went through reads as a failure.
        return repository.get(id, viewedBy)
    }

}

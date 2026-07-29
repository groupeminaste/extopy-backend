package com.extopy.domain.usecases.posts

import com.extopy.domain.exceptions.posts.PostBodyEmptyException
import com.extopy.domain.exceptions.posts.PostReplyAndRepostException
import com.extopy.domain.exceptions.posts.RepliedPostNotFoundException
import com.extopy.domain.exceptions.posts.RepostedPostNotFoundException
import com.extopy.domain.exceptions.validation.ValidationException
import com.extopy.domain.models.posts.Post
import com.extopy.domain.models.posts.PostPayload
import com.extopy.domain.repositories.PostsRepository
import kotlin.uuid.Uuid

class CreatePostUseCaseImpl(
    private val repository: PostsRepository,
) : CreatePostUseCase {

    override suspend fun invoke(payload: PostPayload, userId: Uuid): Post? {
        // A repost is allowed to say nothing; anything else has to.
        if (payload.body.isBlank() && payload.repostOfId == null) throw PostBodyEmptyException()
        if (payload.body.length > Post.BODY_MAX_LENGTH) throw ValidationException("posts_body_maxLength")
        if (payload.repostOfId != null && payload.repliedToId != null) throw PostReplyAndRepostException()
        payload.repliedToId?.let {
            if (repository.get(it, userId) == null) throw RepliedPostNotFoundException()
        }
        payload.repostOfId?.let {
            if (repository.get(it, userId) == null) throw RepostedPostNotFoundException()
        }
        return repository.create(payload, userId)
    }

}

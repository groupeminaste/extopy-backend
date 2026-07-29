package com.extopy.domain.usecases.posts

import com.extopy.domain.models.posts.Post
import com.extopy.domain.models.posts.PostPayload
import kotlin.uuid.Uuid

/**
 * Publishes a post.
 */
interface CreatePostUseCase {

    suspend operator fun invoke(payload: PostPayload, userId: Uuid): Post?

}

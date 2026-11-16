package com.extopy.domain.usecases.posts

import com.extopy.domain.models.posts.Post
import com.extopy.domain.models.posts.PostPayload
import kotlin.uuid.Uuid

/**
 * Rewrites the body of a post, and reads it back.
 */
interface UpdatePostUseCase {

    suspend operator fun invoke(id: Uuid, payload: PostPayload, viewedBy: Uuid): Post?

}

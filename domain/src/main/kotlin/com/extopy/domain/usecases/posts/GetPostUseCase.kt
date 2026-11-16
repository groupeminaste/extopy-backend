package com.extopy.domain.usecases.posts

import com.extopy.domain.models.posts.Post
import kotlin.uuid.Uuid

/**
 * Resolves a post, as the account doing the reading sees it.
 */
interface GetPostUseCase {

    suspend operator fun invoke(id: Uuid, viewedBy: Uuid): Post?

}

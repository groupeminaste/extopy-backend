package com.extopy.domain.usecases.posts

import kotlin.uuid.Uuid

/**
 * Removes the like of an account on a post.
 */
interface DeleteLikeInPostUseCase {

    /**
     * @return True if a like was removed.
     */
    suspend operator fun invoke(postId: Uuid, userId: Uuid): Boolean

}

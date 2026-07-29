package com.extopy.domain.usecases.posts

import kotlin.uuid.Uuid

/**
 * Deletes a post.
 */
interface DeletePostUseCase {

    /**
     * @return True if a post was deleted.
     */
    suspend operator fun invoke(id: Uuid): Boolean

}

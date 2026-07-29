package com.extopy.domain.usecases.users

import kotlin.uuid.Uuid

/**
 * Removes a follow link, which is either unfollowing an account or dropping one of your followers.
 */
interface DeleteFollowerInUserUseCase {

    /**
     * @param userId The account doing the following.
     * @param targetId The account being followed.
     *
     * @return True if a link was removed.
     */
    suspend operator fun invoke(userId: Uuid, targetId: Uuid): Boolean

}

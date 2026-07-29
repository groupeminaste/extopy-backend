package com.extopy.domain.usecases.users

import com.extopy.domain.models.users.FollowerInUser
import kotlin.uuid.Uuid

/**
 * Follows an account.
 */
interface CreateFollowerInUserUseCase {

    /**
     * @param targetId The account being followed.
     * @param userId The account doing the following.
     */
    suspend operator fun invoke(targetId: Uuid, userId: Uuid): FollowerInUser?

}

package com.extopy.domain.repositories

import com.extopy.domain.models.application.Pagination
import com.extopy.domain.models.users.FollowerInUser
import kotlin.uuid.Uuid

/**
 * Reads and writes the follow links between the accounts.
 */
interface FollowersInUsersRepository {

    /**
     * Lists the accounts following [targetId], accepted links only.
     */
    suspend fun list(pagination: Pagination, targetId: Uuid): List<FollowerInUser>

    /**
     * Lists the accounts [userId] follows, accepted links only.
     */
    suspend fun listFollowing(pagination: Pagination, userId: Uuid): List<FollowerInUser>

    /**
     * Adds a follow link.
     *
     * @param accepted Whether the link is live right away, which it is for a public account.
     */
    suspend fun create(userId: Uuid, targetId: Uuid, accepted: Boolean): FollowerInUser?

    /**
     * Removes a follow link.
     *
     * @return True if a row was deleted.
     */
    suspend fun delete(userId: Uuid, targetId: Uuid): Boolean

}

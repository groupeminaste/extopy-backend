package com.extopy.domain.usecases.users

import com.extopy.domain.models.application.Pagination
import com.extopy.domain.models.users.FollowerInUser
import kotlin.uuid.Uuid

/**
 * Lists the accounts following a user.
 */
interface ListFollowersInUserUseCase {

    suspend operator fun invoke(pagination: Pagination, targetId: Uuid): List<FollowerInUser>

}

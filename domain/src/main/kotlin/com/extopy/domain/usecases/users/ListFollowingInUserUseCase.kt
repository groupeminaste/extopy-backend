package com.extopy.domain.usecases.users

import com.extopy.domain.models.application.Pagination
import com.extopy.domain.models.users.FollowerInUser
import kotlin.uuid.Uuid

/**
 * Lists the accounts a user follows.
 */
interface ListFollowingInUserUseCase {

    suspend operator fun invoke(pagination: Pagination, userId: Uuid): List<FollowerInUser>

}

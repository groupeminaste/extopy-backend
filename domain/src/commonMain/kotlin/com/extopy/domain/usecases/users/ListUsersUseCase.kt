package com.extopy.domain.usecases.users

import com.extopy.domain.models.application.Pagination
import com.extopy.domain.models.users.User
import kotlin.uuid.Uuid

/**
 * Lists the accounts, as the account doing the reading sees them.
 */
interface ListUsersUseCase {

    suspend operator fun invoke(pagination: Pagination, viewedBy: Uuid): List<User>

}

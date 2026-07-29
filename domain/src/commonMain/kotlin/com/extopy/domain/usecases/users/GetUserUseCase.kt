package com.extopy.domain.usecases.users

import com.extopy.domain.models.users.User
import kotlin.uuid.Uuid

/**
 * Resolves an account, as the account doing the reading sees it.
 */
interface GetUserUseCase {

    /**
     * @param id The account being read.
     * @param viewedBy The account doing the reading, which decides the follow flags it carries.
     */
    suspend operator fun invoke(id: Uuid, viewedBy: Uuid): User?

}

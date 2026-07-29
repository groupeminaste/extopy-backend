package com.extopy.domain.usecases.users

import com.extopy.domain.models.users.UpdateUserPayload
import com.extopy.domain.models.users.User
import kotlin.uuid.Uuid

/**
 * Changes the fields of an account the payload carries, and reads it back.
 */
interface UpdateUserUseCase {

    suspend operator fun invoke(id: Uuid, payload: UpdateUserPayload): User?

}

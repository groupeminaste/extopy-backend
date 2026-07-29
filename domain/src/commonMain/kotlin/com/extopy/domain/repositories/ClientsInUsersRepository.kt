package com.extopy.domain.repositories

import com.extopy.domain.models.users.ClientInUser
import kotlin.time.Instant
import kotlin.uuid.Uuid

/**
 * Reads and writes the authorization codes handed to the clients.
 */
interface ClientsInUsersRepository {

    /**
     * Hands a client a fresh code for a user.
     *
     * @param code The code that was drawn, which the caller owns so that drawing one stays in a
     *             single place.
     */
    suspend fun create(code: String, userId: Uuid, clientId: Uuid, expiresAt: Instant): ClientInUser?

    /**
     * Reads a code.
     */
    suspend fun get(code: String): ClientInUser?

    /**
     * Burns a code.
     *
     * @return True if a row was deleted.
     */
    suspend fun delete(code: String): Boolean

}

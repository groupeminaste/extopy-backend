package com.extopy.domain.repositories

import com.extopy.domain.models.application.Client
import kotlin.uuid.Uuid

/**
 * Reads the OAuth clients.
 *
 * They are only ever created by hand, straight in the database, so there is nothing to write here.
 */
interface ClientsRepository {

    /**
     * Reads a client.
     */
    suspend fun get(id: Uuid): Client?

}

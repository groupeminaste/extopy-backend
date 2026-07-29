package com.extopy.domain.repositories

/**
 * Keeps the sessions of the web pages, so they survive a restart and are shared between the
 * instances.
 */
interface SessionsRepository {

    /**
     * Reads the payload of a session.
     */
    suspend fun get(id: String): String?

    /**
     * Writes the payload of a session, replacing whatever it held.
     */
    suspend fun set(id: String, value: String)

    /**
     * Ends a session.
     */
    suspend fun delete(id: String)

}

package com.extopy.infrastructure.sessions

import com.extopy.domain.repositories.SessionsRepository
import io.ktor.server.sessions.*

/**
 * Ktor [SessionStorage] keeping the sessions in the database, so they survive a restart and are
 * shared between the instances.
 */
class DatabaseSessionStorage(
    private val repository: SessionsRepository,
) : SessionStorage {

    override suspend fun read(id: String): String =
        repository.get(id) ?: throw NoSuchElementException("Session $id not found")

    override suspend fun write(id: String, value: String) = repository.set(id, value)

    override suspend fun invalidate(id: String) = repository.delete(id)

}

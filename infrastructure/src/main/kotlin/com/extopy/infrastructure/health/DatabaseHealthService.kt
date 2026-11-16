package com.extopy.infrastructure.health

import com.extopy.domain.services.HealthService
import com.extopy.infrastructure.database.DatabaseFactory

/**
 * Implementation of [HealthService] reading the state of the connection pool.
 */
class DatabaseHealthService(
    private val databaseFactory: DatabaseFactory,
) : HealthService {

    override suspend fun isDatabaseHealthy(): Boolean = databaseFactory.isHealthy()

}

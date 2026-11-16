package com.extopy.domain.services

/**
 * The checks the readiness probe reads.
 */
interface HealthService {

    /**
     * Whether the database can be reached.
     */
    suspend fun isDatabaseHealthy(): Boolean

}

package com.extopy.infrastructure.database

import org.jetbrains.exposed.v1.jdbc.Database

/** Factory for obtaining a [Database] instance and checking its health. */
interface DatabaseFactory {

    /** Returns the (lazily connected) [Database] instance. */
    fun getDatabase(): Database

    /** Returns true if the database connection is healthy. */
    fun isHealthy(): Boolean

}

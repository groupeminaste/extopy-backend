package com.extopy.infrastructure.database

import org.jetbrains.exposed.v1.jdbc.Database

/**
 * [DatabaseFactory] for an in-memory H2 database, which is what the tests run against.
 */
class H2DatabaseFactory(
    private val config: DatabaseConfig,
) : DatabaseFactory {

    private val db: Database by lazy {
        Database.connect("jdbc:h2:mem:${config.name};DB_CLOSE_DELAY=-1;MODE=MySQL;", "org.h2.Driver")
    }

    override fun getDatabase(): Database = db

    // An in-memory database is up for as long as the process is.
    override fun isHealthy(): Boolean = true

}

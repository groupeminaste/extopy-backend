package com.extopy.infrastructure.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.jdbc.Database

/**
 * [DatabaseFactory] for MySQL, backed by a HikariCP connection pool.
 *
 * The schema belongs to Flyway: the first time the database is asked for, the pending migrations of
 * `db/migration` are applied, and nothing else ever creates or alters a table.
 */
class MySQLDatabaseFactory(
    private val config: DatabaseConfig,
) : DatabaseFactory {

    private val dataSource: HikariDataSource by lazy {
        HikariDataSource(
            HikariConfig().apply {
                poolName = "hikari-${config.name}"
                jdbcUrl = config.jdbcUrl
                driverClassName = "com.mysql.cj.jdbc.Driver"
                username = config.user
                password = config.password
                isAutoCommit = false
                maximumPoolSize = config.maximumPoolSize
                minimumIdle = 1
                validationTimeout = 3_000
                connectionTimeout = 30_000
                idleTimeout = 300_000
                maxLifetime = 1_800_000
                keepaliveTime = 600_000
                leakDetectionThreshold = 60_000
            }
        )
    }

    private val db: Database by lazy {
        migrate()
        Database.connect(dataSource)
    }

    override fun getDatabase(): Database = db

    override fun isHealthy(): Boolean = !dataSource.isClosed && dataSource.isRunning

    private fun migrate() {
        Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration")
            // A database created before Flyway (by `SchemaUtils.create`, as extopy.dev was) already
            // holds what V1 creates: it is marked as being at V1, and only what comes after runs.
            .baselineOnMigrate(true)
            .baselineVersion("1")
            .load()
            .migrate()
    }

}

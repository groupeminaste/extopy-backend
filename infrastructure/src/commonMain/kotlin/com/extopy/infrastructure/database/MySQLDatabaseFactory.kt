package com.extopy.infrastructure.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.v1.jdbc.Database

/**
 * [DatabaseFactory] for MySQL, backed by a HikariCP connection pool.
 */
class MySQLDatabaseFactory(
    private val config: DatabaseConfig,
) : DatabaseFactory {

    private val dataSource: HikariDataSource by lazy {
        HikariDataSource(
            HikariConfig().apply {
                poolName = "hikari-${config.name}"
                jdbcUrl = "jdbc:mysql://${config.host}:3306/${config.name}"
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

    private val db: Database by lazy { Database.connect(dataSource) }

    override fun getDatabase(): Database = db

    override fun isHealthy(): Boolean = !dataSource.isClosed && dataSource.isRunning

}

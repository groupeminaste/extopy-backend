package com.extopy.infrastructure.database

import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.testcontainers.mysql.MySQLContainer

/**
 * Hands out a [TransactionManager] over a database of its own, on the MySQL that runs in production.
 *
 * One MySQL 8.4 container is started per test JVM (Docker is required), with its data in memory.
 * Each name gets its own database inside it, migrated by Flyway as the server's would be, so tests
 * never read what another one wrote and run against the schema the migrations actually build.
 */
object DatabaseTestHelper {

    private val container: MySQLContainer by lazy {
        MySQLContainer("mysql:8.4")
            .withUsername("root")
            .withPassword("extopy-test")
            .withTmpFs(mapOf("/var/lib/mysql" to "rw,nosuid,size=1g"))
            .also { it.start() }
    }

    private val created = mutableSetOf<String>()

    /** Settings of a fresh, empty database called [name] (created once per JVM). */
    @Synchronized
    fun config(name: String): DatabaseConfig {
        val databaseName = "extopy_" + name.replace(Regex("[^A-Za-z0-9_]"), "_")
        if (created.add(databaseName)) {
            val server = Database.connect(
                url = "jdbc:mysql://${container.host}:${container.firstMappedPort}/",
                user = container.username,
                password = container.password,
            )
            transaction(server) { SchemaUtils.createDatabase(databaseName) }
        }
        return DatabaseConfig(
            host = container.host,
            port = container.firstMappedPort,
            name = databaseName,
            user = container.username,
            password = container.password,
            maximumPoolSize = 2,
        )
    }

    fun transactionManager(name: String): TransactionManager =
        TransactionManagerImpl(MySQLDatabaseFactory(config(name)))

}

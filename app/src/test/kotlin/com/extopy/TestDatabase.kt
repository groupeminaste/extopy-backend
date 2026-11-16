package com.extopy

import io.ktor.server.config.*
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.testcontainers.mysql.MySQLContainer

/**
 * The MySQL the application tests boot against: one MySQL 8.4 container per test JVM (Docker is
 * required), and one database per test inside it, which the application migrates when it starts.
 */
object TestDatabase {

    private val container: MySQLContainer by lazy {
        MySQLContainer("mysql:8.4")
            .withUsername("root")
            .withPassword("extopy-test")
            .withTmpFs(mapOf("/var/lib/mysql" to "rw,nosuid,size=1g"))
            .also { it.start() }
    }

    private val created = mutableSetOf<String>()

    /** `application.test.conf`, pointed at a fresh database called [name]. */
    @Synchronized
    fun config(name: String): ApplicationConfig {
        val databaseName = "extopy_" + name.replace(Regex("[^A-Za-z0-9_]"), "_")
        if (created.add(databaseName)) {
            val server = Database.connect(
                url = "jdbc:mysql://${container.host}:${container.firstMappedPort}/",
                user = container.username,
                password = container.password,
            )
            transaction(server) { SchemaUtils.createDatabase(databaseName) }
        }
        return ApplicationConfig("application.test.conf").mergeWith(
            MapApplicationConfig(
                "database.host" to container.host,
                "database.port" to container.firstMappedPort.toString(),
                "database.name" to databaseName,
                "database.user" to container.username,
                "database.password" to container.password,
            )
        )
    }

}

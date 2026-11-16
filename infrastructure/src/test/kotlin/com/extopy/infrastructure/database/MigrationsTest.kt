package com.extopy.infrastructure.database

import com.extopy.infrastructure.database.tables.*
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils
import kotlin.time.Clock
import kotlin.uuid.Uuid
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.LocalDate

/**
 * The migrations and the Exposed tables describe the same schema.
 *
 * When this fails, a table was changed without its migration: run
 * `./gradlew :infrastructure:generateMigrations`, review the file it writes, and commit it.
 */
class MigrationsTest {

    private val tables = arrayOf(
        Clients,
        ClientsInUsers,
        CodesInEmails,
        FollowersInUsers,
        LikesInPosts,
        Posts,
        Sessions,
        Users,
    )

    @Test
    fun testMigrationsBuildTheSchemaOfTheTables() {
        val missing = DatabaseTestHelper.transactionManager("migrations").transaction {
            MigrationUtils.statementsRequiredForDatabaseMigration(*tables, withLogs = false)
        }
        assertEquals(emptyList(), missing)
    }


    /**
     * A database built before Flyway, as extopy.dev was: its tables came from `SchemaUtils.create`.
     * Flyway marks it as being at V1 instead of creating the tables again, and keeps its rows.
     */
    @Test
    fun testADatabaseCreatedBeforeFlywayIsBaselined() {
        val config = DatabaseTestHelper.config("before_flyway")
        val legacy = Database.connect(config.jdbcUrl, user = config.user, password = config.password)
        val userId = Uuid.random()
        transaction(legacy) {
            SchemaUtils.create(*tables)
            Users.insert {
                it[id] = userId
                it[displayName] = "Legacy"
                it[username] = "legacy"
                it[email] = "legacy@extopy.com"
                it[password] = "hash"
                it[biography] = "Hello"
                it[birthdate] = LocalDate(2000, 1, 1)
                it[joinDate] = Clock.System.now()
                it[lastActive] = Clock.System.now()
            }
        }

        val manager = TransactionManagerImpl(MySQLDatabaseFactory(config))
        val (users, missing) = manager.transaction {
            Users.selectAll().count() to MigrationUtils.statementsRequiredForDatabaseMigration(*tables, withLogs = false)
        }
        assertEquals(1, users)
        assertEquals(emptyList(), missing)
    }

}

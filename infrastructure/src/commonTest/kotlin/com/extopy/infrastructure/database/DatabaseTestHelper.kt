package com.extopy.infrastructure.database

/**
 * Hands out a [TransactionManager] over its own in-memory database.
 *
 * Each one gets a name of its own: `DB_CLOSE_DELAY=-1` keeps a database alive for as long as the
 * process is, so sharing a name would let one test read what another one wrote.
 */
object DatabaseTestHelper {

    fun transactionManager(name: String): TransactionManager = TransactionManagerImpl(
        H2DatabaseFactory(
            DatabaseConfig(
                protocol = "h2",
                host = "",
                name = name,
                user = "",
                password = "",
            )
        )
    )

}

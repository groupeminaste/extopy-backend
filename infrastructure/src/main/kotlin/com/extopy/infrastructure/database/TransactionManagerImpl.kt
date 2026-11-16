package com.extopy.infrastructure.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.core.Transaction

/**
 * [TransactionManager] backed by Exposed.
 */
class TransactionManagerImpl(
    private val databaseFactory: DatabaseFactory,
) : TransactionManager {

    override fun <T> transaction(statement: Transaction.() -> T): T =
        org.jetbrains.exposed.v1.jdbc.transactions.transaction(databaseFactory.getDatabase()) { statement() }

    override suspend fun <T> suspendTransaction(statement: suspend Transaction.() -> T): T =
        withContext(Dispatchers.IO) {
            org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction(databaseFactory.getDatabase()) {
                statement()
            }
        }

}

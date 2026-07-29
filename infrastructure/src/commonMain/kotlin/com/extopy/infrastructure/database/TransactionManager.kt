package com.extopy.infrastructure.database

import org.jetbrains.exposed.v1.core.Transaction

/** Manages database transactions. */
interface TransactionManager {

    /** Executes [statement] within a database transaction. */
    fun <T> transaction(statement: Transaction.() -> T): T

    /** Executes [statement] within a suspendable database transaction. */
    suspend fun <T> suspendTransaction(statement: suspend Transaction.() -> T): T

}

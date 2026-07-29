package com.extopy.infrastructure.database.repositories

import com.extopy.domain.repositories.SessionsRepository
import com.extopy.infrastructure.database.TransactionManager
import com.extopy.infrastructure.database.tables.Sessions
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update

class SessionsDatabaseRepository(
    private val transactionManager: TransactionManager,
) : SessionsRepository {

    init {
        transactionManager.transaction {
            SchemaUtils.create(Sessions)
        }
    }

    override suspend fun get(id: String): String? =
        transactionManager.suspendTransaction {
            Sessions
                .selectAll()
                .where { Sessions.id eq id }
                .map { it[Sessions.value] }
                .firstOrNull()
        }

    override suspend fun set(id: String, value: String) {
        transactionManager.suspendTransaction {
            val updated = Sessions.update({ Sessions.id eq id }) {
                it[Sessions.value] = value
            }
            if (updated == 0) Sessions.insert {
                it[Sessions.id] = id
                it[Sessions.value] = value
            }
        }
    }

    override suspend fun delete(id: String) {
        transactionManager.suspendTransaction {
            Sessions.deleteWhere { Sessions.id eq id }
        }
    }

}

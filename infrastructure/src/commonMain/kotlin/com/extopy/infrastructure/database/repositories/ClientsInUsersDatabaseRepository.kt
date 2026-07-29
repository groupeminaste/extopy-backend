package com.extopy.infrastructure.database.repositories

import com.extopy.domain.models.users.ClientInUser
import com.extopy.domain.repositories.ClientsInUsersRepository
import com.extopy.infrastructure.database.TransactionManager
import com.extopy.infrastructure.database.tables.ClientsInUsers
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import kotlin.time.Instant
import kotlin.uuid.Uuid

class ClientsInUsersDatabaseRepository(
    private val transactionManager: TransactionManager,
) : ClientsInUsersRepository {

    init {
        transactionManager.transaction {
            SchemaUtils.create(ClientsInUsers)
        }
    }

    override suspend fun create(code: String, userId: Uuid, clientId: Uuid, expiresAt: Instant): ClientInUser? =
        transactionManager.suspendTransaction {
            ClientsInUsers.insert {
                it[ClientsInUsers.code] = code
                it[ClientsInUsers.userId] = userId
                it[ClientsInUsers.clientId] = clientId
                it[ClientsInUsers.expiresAt] = expiresAt
            }.resultedValues?.map(ClientsInUsers::toClientInUser)?.firstOrNull()
        }

    override suspend fun get(code: String): ClientInUser? =
        transactionManager.suspendTransaction {
            ClientsInUsers
                .selectAll()
                .where { ClientsInUsers.code eq code }
                .map(ClientsInUsers::toClientInUser)
                .firstOrNull()
        }

    override suspend fun delete(code: String): Boolean =
        transactionManager.suspendTransaction {
            ClientsInUsers.deleteWhere { ClientsInUsers.code eq code }
        } == 1

}

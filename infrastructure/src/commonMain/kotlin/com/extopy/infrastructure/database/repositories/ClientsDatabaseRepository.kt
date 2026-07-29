package com.extopy.infrastructure.database.repositories

import com.extopy.domain.models.application.Client
import com.extopy.domain.repositories.ClientsRepository
import com.extopy.infrastructure.database.TransactionManager
import com.extopy.infrastructure.database.tables.Clients
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.selectAll
import kotlin.uuid.Uuid

class ClientsDatabaseRepository(
    private val transactionManager: TransactionManager,
) : ClientsRepository {

    init {
        transactionManager.transaction {
            SchemaUtils.create(Clients)
        }
    }

    override suspend fun get(id: Uuid): Client? =
        transactionManager.suspendTransaction {
            Clients
                .selectAll()
                .where { Clients.id eq id }
                .map(Clients::toClient)
                .firstOrNull()
        }

}

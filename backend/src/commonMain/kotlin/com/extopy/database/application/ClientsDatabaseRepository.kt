package com.extopy.database.application

import com.extopy.models.application.Client
import dev.kaccelero.database.IDatabase
import dev.kaccelero.models.IContext
import dev.kaccelero.repositories.IModelSuspendRepository
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.selectAll

class ClientsDatabaseRepository(
    private val database: IDatabase,
) : IModelSuspendRepository<Client, String, Unit, Unit> {

    init {
        database.transaction {
            SchemaUtils.create(Clients)
        }
    }

    override suspend fun get(id: String, context: IContext?): Client? =
        database.suspendedTransaction {
            Clients
                .selectAll()
                .where { Clients.id eq id }
                .map(Clients::toClient)
                .singleOrNull()
        }

}

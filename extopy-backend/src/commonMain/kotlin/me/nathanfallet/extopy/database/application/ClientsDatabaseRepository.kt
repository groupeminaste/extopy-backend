package me.nathanfallet.extopy.database.application

import me.nathanfallet.extopy.models.application.Client
import me.nathanfallet.surexposed.database.IDatabase
import me.nathanfallet.usecases.context.IContext
import me.nathanfallet.usecases.models.repositories.IModelSuspendRepository
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

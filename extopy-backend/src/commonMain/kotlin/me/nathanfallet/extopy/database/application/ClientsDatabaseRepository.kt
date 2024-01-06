package me.nathanfallet.extopy.database.application

import me.nathanfallet.extopy.models.application.Client
import me.nathanfallet.ktorx.database.IDatabase
import me.nathanfallet.usecases.context.IContext
import me.nathanfallet.usecases.models.repositories.IModelSuspendRepository
import org.jetbrains.exposed.sql.selectAll

class ClientsDatabaseRepository(
    private val database: IDatabase,
) : IModelSuspendRepository<Client, String, Unit, Unit> {

    override suspend fun get(id: String, context: IContext?): Client? {
        return database.dbQuery {
            Clients
                .selectAll()
                .where { Clients.id eq id }
                .map(Clients::toClient)
                .singleOrNull()
        }
    }

}

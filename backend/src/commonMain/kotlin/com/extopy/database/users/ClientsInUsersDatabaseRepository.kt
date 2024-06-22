package com.extopy.database.users

import kotlinx.datetime.Instant
import com.extopy.models.users.ClientInUser
import com.extopy.repositories.users.IClientsInUsersRepository
import dev.kaccelero.database.IDatabase
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

class ClientsInUsersDatabaseRepository(
    private val database: IDatabase,
) : IClientsInUsersRepository {

    init {
        database.transaction {
            SchemaUtils.create(ClientsInUsers)
        }
    }

    override suspend fun create(userId: String, clientId: String, expiration: Instant): ClientInUser? =
        database.suspendedTransaction {
            ClientsInUsers.insert {
                it[code] = generateCode()
                it[ClientsInUsers.userId] = userId
                it[ClientsInUsers.clientId] = clientId
                it[ClientsInUsers.expiration] = expiration.toString()
            }.resultedValues?.map(ClientsInUsers::toClientInUser)?.singleOrNull()
        }

    override suspend fun get(code: String): ClientInUser? =
        database.suspendedTransaction {
            ClientsInUsers
                .selectAll()
                .where { ClientsInUsers.code eq code }
                .map(ClientsInUsers::toClientInUser)
                .singleOrNull()
        }

    override suspend fun delete(code: String): Boolean =
        database.suspendedTransaction {
            ClientsInUsers.deleteWhere {
                ClientsInUsers.code eq code
            }
        } == 1

}

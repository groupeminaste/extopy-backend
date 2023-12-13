package me.nathanfallet.extopy.database.application

import kotlinx.coroutines.runBlocking
import me.nathanfallet.extopy.database.Database
import org.jetbrains.exposed.sql.insert
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class DatabaseClientsRepositoryTest {

    @Test
    fun getClient() = runBlocking {
        val database = Database(protocol = "h2", name = "getClient")
        val repository = DatabaseClientsRepository(database)
        val client = database.dbQuery {
            Clients.insert {
                it[id] = "id"
                it[ownerId] = "ownerId"
                it[name] = "name"
                it[secret] = "secret"
                it[redirectUri] = "redirectUri"
            }.resultedValues?.map(Clients::toClient)?.singleOrNull()
        } ?: fail("Unable to create client")
        val result = repository.get(client.id)
        assertEquals(client.id, result?.id)
        assertEquals(client.ownerId, result?.ownerId)
        assertEquals(client.name, result?.name)
        assertEquals(client.secret, result?.secret)
        assertEquals(client.redirectUri, result?.redirectUri)
    }

}

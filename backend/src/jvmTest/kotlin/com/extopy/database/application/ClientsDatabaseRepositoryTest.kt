package com.extopy.database.application

import com.extopy.database.Database
import dev.kaccelero.database.set
import dev.kaccelero.models.UUID
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.insert
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ClientsDatabaseRepositoryTest {

    @Test
    fun getClient() = runBlocking {
        val database = Database(protocol = "h2", name = "getClient")
        val repository = ClientsDatabaseRepository(database)
        val client = database.suspendedTransaction {
            Clients.insert {
                it[ownerId] = UUID()
                it[name] = "name"
                it[description] = "description"
                it[secret] = "secret"
                it[redirectUri] = "redirectUri"
            }.resultedValues?.map(Clients::toClient)?.singleOrNull()
        } ?: fail("Unable to create client")
        val result = repository.get(client.id)
        assertEquals(client.id, result?.id)
        assertEquals(client.ownerId, result?.ownerId)
        assertEquals(client.name, result?.name)
        assertEquals(client.description, result?.description)
        assertEquals(client.secret, result?.secret)
        assertEquals(client.redirectUri, result?.redirectUri)
    }

}

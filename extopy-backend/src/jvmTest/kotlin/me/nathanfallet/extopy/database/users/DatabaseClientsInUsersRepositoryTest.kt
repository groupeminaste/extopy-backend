package me.nathanfallet.extopy.database.users

import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import me.nathanfallet.extopy.database.Database
import org.jetbrains.exposed.sql.selectAll
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class DatabaseClientsInUsersRepositoryTest {

    private val now = Clock.System.now()

    @Test
    fun createClientInUser() = runBlocking {
        val database = Database(protocol = "h2", name = "createClientInUser")
        val repository = DatabaseClientsInUsersRepository(database)
        val clientInUser = repository.create("userId", "clientId", now)
        val clientInUserFromDatabase = database.dbQuery {
            ClientsInUsers
                .selectAll()
                .map(ClientsInUsers::toClientInUser)
                .singleOrNull()
        }
        assertEquals(clientInUserFromDatabase?.code, clientInUser?.code)
        assertEquals(clientInUserFromDatabase?.userId, clientInUser?.userId)
        assertEquals(clientInUserFromDatabase?.clientId, clientInUser?.clientId)
        assertEquals(clientInUserFromDatabase?.expiration, clientInUser?.expiration)
        assertEquals(clientInUserFromDatabase?.userId, "userId")
        assertEquals(clientInUserFromDatabase?.clientId, "clientId")
        assertEquals(clientInUserFromDatabase?.expiration, now)
    }

    @Test
    fun getClientInUser() = runBlocking {
        val database = Database(protocol = "h2", name = "getClientInUser")
        val repository = DatabaseClientsInUsersRepository(database)
        val clientInUser = repository.create(
            "userId", "clientId", now
        ) ?: fail("Unable to create clientInUser")
        val result = repository.get(clientInUser.code)
        assertEquals(clientInUser.code, result?.code)
        assertEquals(clientInUser.userId, result?.userId)
        assertEquals(clientInUser.clientId, result?.clientId)
        assertEquals(clientInUser.expiration, result?.expiration)
    }

    @Test
    fun getClientInUserNotFound() = runBlocking {
        val database = Database(protocol = "h2", name = "getClientInUserNotFound")
        val repository = DatabaseClientsInUsersRepository(database)
        val result = repository.get("code")
        assertEquals(null, result)
    }

    @Test
    fun deleteClientInUser() = runBlocking {
        val database = Database(protocol = "h2", name = "deleteClientInUser")
        val repository = DatabaseClientsInUsersRepository(database)
        val clientInUser = repository.create(
            "userId", "clientId", now
        ) ?: fail("Unable to create clientInUser")
        assertEquals(true, repository.delete(clientInUser.code))
        val count = database.dbQuery {
            ClientsInUsers.selectAll().count()
        }
        assertEquals(0, count)
    }

    @Test
    fun deleteClientInUserNotFound() = runBlocking {
        val database = Database(protocol = "h2", name = "deleteClientInUserNotFound")
        val repository = DatabaseClientsInUsersRepository(database)
        assertEquals(false, repository.delete("code"))
    }

    @Test
    fun deleteClientInUserWrong() = runBlocking {
        val database = Database(protocol = "h2", name = "deleteClientInUserWrong")
        val repository = DatabaseClientsInUsersRepository(database)
        repository.create(
            "userId", "clientId", now
        ) ?: fail("Unable to create clientInUser")
        assertEquals(false, repository.delete("code"))
        val count = database.dbQuery {
            ClientsInUsers.selectAll().count()
        }
        assertEquals(1, count)
    }

}

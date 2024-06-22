package com.extopy.database.users

import com.extopy.database.Database
import com.extopy.database.posts.PostsDatabaseRepository
import com.extopy.models.users.CreateUserPayload
import com.extopy.models.users.UpdateUserPayload
import com.extopy.models.users.UserContext
import com.extopy.repositories.users.IUsersRepository
import dev.kaccelero.database.IDatabase
import dev.kaccelero.models.UUID
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.selectAll
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class UsersDatabaseRepositoryTest {

    private fun createRepository(database: IDatabase): IUsersRepository {
        FollowersInUsersDatabaseRepository(database)
        PostsDatabaseRepository(database)
        return UsersDatabaseRepository(database)
    }

    @Test
    fun createUser() = runBlocking {
        val database = Database(protocol = "h2", name = "createUser")
        val repository = createRepository(database)
        val user = repository.create(
            CreateUserPayload(
                "username", "displayName", "email", "password",
                LocalDate(2002, 12, 24)
            )
        )
        val userFromDatabase = database.suspendedTransaction {
            Users
                .selectAll()
                .map {
                    Users.toUser(it, true)
                }
                .singleOrNull()
        }
        assertEquals(userFromDatabase?.id, user?.id)
        assertEquals(userFromDatabase?.username, user?.username)
        assertEquals(userFromDatabase?.displayName, user?.displayName)
        assertEquals(userFromDatabase?.email, user?.email)
        assertEquals(null, user?.password)
        assertEquals(userFromDatabase?.birthdate, user?.birthdate)
        assertEquals(userFromDatabase?.username, "username")
        assertEquals(userFromDatabase?.displayName, "displayName")
        assertEquals(userFromDatabase?.email, "email")
        assertEquals(userFromDatabase?.password, "password")
        assertEquals(userFromDatabase?.birthdate, LocalDate(2002, 12, 24))
    }

    @Test
    fun getUser() = runBlocking {
        val database = Database(protocol = "h2", name = "getUser")
        val repository = createRepository(database)
        val user = repository.create(
            CreateUserPayload(
                "username", "displayName", "email", "password",
                LocalDate(2002, 12, 24)
            )
        ) ?: fail("Unable to create user")
        val result = repository.get(user.id, UserContext(user.id))
        assertEquals(user.id, result?.id)
        assertEquals(user.username, result?.username)
        assertEquals(user.displayName, result?.displayName)
        assertEquals(null, result?.email)
        assertEquals(null, result?.password)
        assertEquals(user.birthdate, result?.birthdate)
    }

    @Test
    fun getUserNotExists() = runBlocking {
        val database = Database(protocol = "h2", name = "getUserNotExists")
        val repository = createRepository(database)
        val user = repository.create(
            CreateUserPayload(
                "username", "displayName", "email", "password",
                LocalDate(2002, 12, 24)
            )
        ) ?: fail("Unable to create user")
        assertEquals(null, repository.get(UUID(), UserContext(user.id)))
    }

    @Test
    fun getUserNoContext() = runBlocking {
        val database = Database(protocol = "h2", name = "getUserNoContext")
        val repository = createRepository(database)
        val user = repository.create(
            CreateUserPayload(
                "username", "displayName", "email", "password",
                LocalDate(2002, 12, 24)
            )
        ) ?: fail("Unable to create user")
        assertEquals(null, repository.get(user.id))
    }

    @Test
    fun getUserForEmail() = runBlocking {
        val database = Database(protocol = "h2", name = "getUserForEmail")
        val repository = createRepository(database)
        val user = repository.create(
            CreateUserPayload(
                "username", "displayName", "email", "password",
                LocalDate(2002, 12, 24)
            )
        ) ?: fail("Unable to create user")
        val result = repository.getForUsernameOrEmail(user.email!!, false)
        assertEquals(user.id, result?.id)
        assertEquals(user.username, result?.username)
        assertEquals(user.displayName, result?.displayName)
        assertEquals(user.email, result?.email)
        assertEquals(null, result?.password)
        assertEquals(user.birthdate, result?.birthdate)
    }

    @Test
    fun getUserForEmailWithPassword() = runBlocking {
        val database = Database(protocol = "h2", name = "getUserForEmailWithPassword")
        val repository = createRepository(database)
        val user = repository.create(
            CreateUserPayload(
                "username", "displayName", "email", "password",
                LocalDate(2002, 12, 24)
            )
        ) ?: fail("Unable to create user")
        val result = repository.getForUsernameOrEmail(user.email!!, true)
        assertEquals(user.id, result?.id)
        assertEquals(user.username, result?.username)
        assertEquals(user.displayName, result?.displayName)
        assertEquals(user.email, result?.email)
        assertEquals("password", result?.password)
        assertEquals(user.birthdate, result?.birthdate)
    }

    @Test
    fun updateUser() = runBlocking {
        val database = Database(protocol = "h2", name = "updateUser")
        val repository = createRepository(database)
        val user = repository.create(
            CreateUserPayload(
                "username", "displayName", "email", "password",
                LocalDate(2002, 12, 24)
            )
        ) ?: fail("Unable to create user")
        val updatedUser = user.copy(
            username = "username2",
            displayName = "displayName2",
            biography = "biography2",
        )
        val payload = UpdateUserPayload("username2", "displayName2", null, "biography2")
        assertEquals(true, repository.update(user.id, payload))
        val userFromDatabase = database.suspendedTransaction {
            Users
                .selectAll()
                .map {
                    Users.toUser(it, true)
                }
                .singleOrNull()
        }
        assertEquals(userFromDatabase?.id, updatedUser.id)
        assertEquals(userFromDatabase?.email, updatedUser.email)
        assertEquals(userFromDatabase?.displayName, updatedUser.displayName)
        assertEquals(userFromDatabase?.username, updatedUser.username)
        assertEquals(userFromDatabase?.biography, updatedUser.biography)
    }

    @Test
    fun updateUserNotExists() = runBlocking {
        val database = Database(protocol = "h2", name = "updateUserNotExists")
        val repository = createRepository(database)
        val payload = UpdateUserPayload("username2", "displayName2", null, "biography2")
        assertEquals(false, repository.update(UUID(), payload))
    }

}

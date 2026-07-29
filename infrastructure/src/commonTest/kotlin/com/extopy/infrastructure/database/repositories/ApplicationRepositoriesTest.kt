package com.extopy.infrastructure.database.repositories

import com.extopy.infrastructure.database.DatabaseTestHelper
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.uuid.Uuid

/**
 * The small repositories: the codes, the sessions, and the authorization codes.
 */
class ApplicationRepositoriesTest {

    private val userId = Uuid.parse("00000000-0000-4000-8000-000000000001")
    private val clientId = Uuid.parse("00000000-0000-4000-8000-000000000002")

    @Test
    fun testCodeInEmail() = runTest {
        val repository = CodesInEmailsDatabaseRepository(DatabaseTestHelper.transactionManager("codes"))
        val expiresAt = Clock.System.now() + 1.hours

        repository.set("someone@extopy.com", "abc", expiresAt)

        val read = assertNotNull(repository.get("abc"))
        assertEquals("someone@extopy.com", read.email)
        assertEquals("abc", read.code)
    }

    /**
     * There is one code per address at a time, so asking for a second one replaces the first rather
     * than failing on the key.
     */
    @Test
    fun testAskingForASecondCodeReplacesTheFirst() = runTest {
        val repository = CodesInEmailsDatabaseRepository(DatabaseTestHelper.transactionManager("codes-replace"))
        val expiresAt = Clock.System.now() + 1.hours

        repository.set("someone@extopy.com", "first", expiresAt)
        repository.set("someone@extopy.com", "second", expiresAt)

        assertNull(repository.get("first"))
        assertEquals("someone@extopy.com", repository.get("second")?.email)
    }

    @Test
    fun testBurningACodeInEmail() = runTest {
        val repository = CodesInEmailsDatabaseRepository(DatabaseTestHelper.transactionManager("codes-delete"))
        repository.set("someone@extopy.com", "abc", Clock.System.now() + 1.hours)

        repository.delete("abc")

        assertNull(repository.get("abc"))
    }

    @Test
    fun testAuthorizationCode() = runTest {
        val repository = ClientsInUsersDatabaseRepository(DatabaseTestHelper.transactionManager("clients-in-users"))
        val expiresAt = Clock.System.now() + 1.hours

        val created = assertNotNull(repository.create("code", userId, clientId, expiresAt))
        assertEquals(userId, created.userId)
        assertEquals(clientId, created.clientId)

        val read = assertNotNull(repository.get("code"))
        assertEquals(userId, read.userId)

        assertTrue(repository.delete("code"))
        assertFalse(repository.delete("code"))
        assertNull(repository.get("code"))
    }

    @Test
    fun testSessions() = runTest {
        val repository = SessionsDatabaseRepository(DatabaseTestHelper.transactionManager("sessions"))

        assertNull(repository.get("id"))

        repository.set("id", "first")
        assertEquals("first", repository.get("id"))

        // Writing again replaces what was there rather than failing on the key.
        repository.set("id", "second")
        assertEquals("second", repository.get("id"))

        repository.delete("id")
        assertNull(repository.get("id"))
    }

}

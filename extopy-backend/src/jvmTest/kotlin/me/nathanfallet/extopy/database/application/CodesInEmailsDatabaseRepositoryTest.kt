package me.nathanfallet.extopy.database.application

import kotlinx.coroutines.runBlocking
import kotlinx.datetime.*
import me.nathanfallet.extopy.database.Database
import org.jetbrains.exposed.sql.selectAll
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class CodesInEmailsDatabaseRepositoryTest {

    private val now = Clock.System.now()
    private val tomorrow = now.plus(1, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
    private val yesterday = now.minus(1, DateTimeUnit.DAY, TimeZone.currentSystemDefault())

    @Test
    fun getCodeInEmail() = runBlocking {
        val database = Database(protocol = "h2", name = "getCodeInEmail")
        val repository = CodesInEmailsDatabaseRepository(database)
        val codeInEmail = repository.createCodeInEmail("email", "code", tomorrow)
            ?: fail("Unable to create code in email")
        val codeInEmailFromDatabase = repository.getCodeInEmail(codeInEmail.code)
        assertEquals(codeInEmailFromDatabase?.email, codeInEmail.email)
        assertEquals(codeInEmailFromDatabase?.code, codeInEmail.code)
        assertEquals(codeInEmailFromDatabase?.expiresAt, codeInEmail.expiresAt)
    }

    @Test
    fun getCodesInEmailsExpiringBefore() = runBlocking {
        val database = Database(protocol = "h2", name = "getCodesInEmailsExpiringBefore")
        val repository = CodesInEmailsDatabaseRepository(database)
        val codeInEmail = repository.createCodeInEmail("email", "code", yesterday)
            ?: fail("Unable to create code in email")
        repository.createCodeInEmail("email2", "code", tomorrow)
            ?: fail("Unable to create code in email")
        val codesInEmails = repository.getCodesInEmailsExpiringBefore(now)
        assertEquals(codesInEmails.size, 1)
        assertEquals(codesInEmails.first().email, codeInEmail.email)
        assertEquals(codesInEmails.first().code, codeInEmail.code)
        assertEquals(codesInEmails.first().expiresAt, codeInEmail.expiresAt)
    }

    @Test
    fun createCodeInEmail() = runBlocking {
        val database = Database(protocol = "h2", name = "createCodeInEmail")
        val repository = CodesInEmailsDatabaseRepository(database)
        val codeInEmail = repository.createCodeInEmail("email", "code", tomorrow)
        val codeInEmailFromDatabase = database.suspendedTransaction {
            CodesInEmails
                .selectAll()
                .map(CodesInEmails::toCodeInEmail)
                .singleOrNull()
        }
        assertEquals(codeInEmailFromDatabase?.email, codeInEmail?.email)
        assertEquals(codeInEmailFromDatabase?.code, codeInEmail?.code)
        assertEquals(codeInEmailFromDatabase?.expiresAt, codeInEmail?.expiresAt)
    }

    @Test
    fun updateCodeInEmail() = runBlocking {
        val database = Database(protocol = "h2", name = "updateCodeInEmail")
        val repository = CodesInEmailsDatabaseRepository(database)
        val codeInEmail = repository.createCodeInEmail("email", "code", tomorrow)
            ?: fail("Unable to create code in email")
        assertEquals(
            true,
            repository.updateCodeInEmail("email", "newCode", tomorrow)
        )
        val codeInEmailFromDatabase = database.suspendedTransaction {
            CodesInEmails
                .selectAll()
                .map(CodesInEmails::toCodeInEmail)
                .singleOrNull()
        }
        assertEquals(codeInEmailFromDatabase?.email, codeInEmail.email)
        assertEquals(codeInEmailFromDatabase?.code, "newCode")
        assertEquals(codeInEmailFromDatabase?.expiresAt, codeInEmail.expiresAt)
    }

    @Test
    fun deleteCodeInEmail() = runBlocking {
        val database = Database(protocol = "h2", name = "deleteCodeInEmail")
        val repository = CodesInEmailsDatabaseRepository(database)
        val codeInEmail = repository.createCodeInEmail("email", "code", tomorrow)
            ?: fail("Unable to create code in email")
        repository.deleteCodeInEmail(codeInEmail.code)
        val count = database.suspendedTransaction {
            CodesInEmails
                .selectAll()
                .count()
        }
        assertEquals(0, count)
    }

}

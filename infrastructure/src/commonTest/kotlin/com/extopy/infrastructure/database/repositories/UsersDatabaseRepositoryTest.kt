package com.extopy.infrastructure.database.repositories

import com.extopy.domain.models.application.Pagination
import com.extopy.domain.models.users.CreateUserPayload
import com.extopy.domain.models.users.UpdateUserPayload
import com.extopy.domain.models.users.User
import com.extopy.infrastructure.database.DatabaseTestHelper
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UsersDatabaseRepositoryTest {

    private fun repository(name: String) =
        UsersDatabaseRepository(DatabaseTestHelper.transactionManager("users-$name"))

    private fun payload(username: String = "nathanfallet", email: String = "nathan@extopy.com") =
        CreateUserPayload(
            username = username,
            displayName = "Nathan Fallet",
            email = email,
            password = "hashed",
            birthdate = LocalDate.parse("2002-12-24"),
        )

    @Test
    fun testCreateThenGet() = runTest {
        val repository = repository("create")

        val created = assertNotNull(repository.create(payload()))
        val read = repository.get(created.id, created.id)

        assertEquals(created.id, read?.id)
        assertEquals("nathanfallet", read?.username)
        assertEquals(User.DEFAULT_BIOGRAPHY, read?.biography)
        assertEquals(User.DEFAULT_AVATAR, read?.avatar)
    }

    /**
     * Reading an account that is not there must come back empty rather than answer with whichever
     * row happened to be first.
     */
    @Test
    fun testGetOfAnUnknownAccount() = runTest {
        val repository = repository("get-unknown")
        val created = assertNotNull(repository.create(payload()))
        repository.create(payload(username = "someoneelse", email = "someone@extopy.com"))

        val read = repository.get(created.id, created.id)

        assertEquals(created.id, read?.id)
    }

    /**
     * Signing in goes through either the username or the address, and only the check that needs it
     * reads the hash.
     */
    @Test
    fun testGetForUsernameOrEmail() = runTest {
        val repository = repository("username-or-email")
        repository.create(payload())

        assertEquals("nathanfallet", repository.getForUsernameOrEmail("nathanfallet", false)?.username)
        assertEquals("nathanfallet", repository.getForUsernameOrEmail("nathan@extopy.com", false)?.username)
        assertNull(repository.getForUsernameOrEmail("nobody", false))

        assertNull(repository.getForUsernameOrEmail("nathanfallet", false)?.password)
        assertEquals("hashed", repository.getForUsernameOrEmail("nathanfallet", true)?.password)
    }

    @Test
    fun testUpdate() = runTest {
        val repository = repository("update")
        val created = assertNotNull(repository.create(payload()))

        assertTrue(repository.update(created.id, UpdateUserPayload(biography = "Hi", personal = true)))

        val read = repository.get(created.id, created.id)
        assertEquals("Hi", read?.biography)
        assertEquals(true, read?.personal)
        // Everything the payload left out is kept as it was.
        assertEquals("nathanfallet", read?.username)
    }

    /**
     * An update with nothing to set is not a statement any database will take, so it never becomes
     * one.
     */
    @Test
    fun testUpdateWithNothingToSet() = runTest {
        val repository = repository("update-empty")
        val created = assertNotNull(repository.create(payload()))

        assertTrue(repository.update(created.id, UpdateUserPayload()))

        assertEquals("nathanfallet", repository.get(created.id, created.id)?.username)
    }

    @Test
    fun testList() = runTest {
        val repository = repository("list")
        repository.create(payload())
        val second = assertNotNull(repository.create(payload(username = "someoneelse", email = "someone@extopy.com")))

        val all = repository.list(Pagination(), second.id)

        assertEquals(2, all.size)
    }

    /**
     * The search matches any of the words against the username, the display name and the biography.
     */
    @Test
    fun testListWithASearch() = runTest {
        val repository = repository("search")
        val first = assertNotNull(repository.create(payload()))
        repository.create(payload(username = "someoneelse", email = "someone@extopy.com"))

        val found = repository.list(Pagination(search = "nathanfallet"), first.id)

        assertEquals(1, found.size)
        assertEquals("nathanfallet", found.first().username)
    }

    /**
     * The wildcards of `LIKE` are escaped, so that a search for `%` is a search and not a
     * match-everything.
     */
    @Test
    fun testTheSearchDoesNotTakeWildcards() = runTest {
        val repository = repository("search-wildcards")
        val first = assertNotNull(repository.create(payload()))
        repository.create(payload(username = "someoneelse", email = "someone@extopy.com"))

        assertEquals(0, repository.list(Pagination(search = "%"), first.id).size)
    }

    @Test
    fun testListIsPaginated() = runTest {
        val repository = repository("pagination")
        val first = assertNotNull(repository.create(payload()))
        repository.create(payload(username = "someoneelse", email = "someone@extopy.com"))
        repository.create(payload(username = "athird", email = "third@extopy.com"))

        assertEquals(2, repository.list(Pagination(limit = 2), first.id).size)
        assertEquals(1, repository.list(Pagination(limit = 2, offset = 2), first.id).size)
    }

}

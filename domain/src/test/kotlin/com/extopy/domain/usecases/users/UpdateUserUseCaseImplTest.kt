package com.extopy.domain.usecases.users

import com.extopy.domain.exceptions.auth.UsernameAlreadyTakenException
import com.extopy.domain.exceptions.validation.ValidationException
import com.extopy.domain.models.users.UpdateUserPayload
import com.extopy.domain.models.users.User
import com.extopy.domain.repositories.UsersRepository
import com.extopy.domain.services.PasswordEncoderService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.uuid.Uuid

class UpdateUserUseCaseImplTest {

    private val id = Uuid.parse("00000000-0000-4000-8000-000000000001")
    private val otherId = Uuid.parse("00000000-0000-4000-8000-000000000002")

    private val user = User(id = id, displayName = "Nathan Fallet", username = "nathanfallet")

    /**
     * Reading the account back has to carry the account doing the reading, or the query returns
     * nothing and a write that went through reads as a failure.
     */
    @Test
    fun testReadsTheAccountBackAsItself() = runTest {
        val repository = mockk<UsersRepository>()
        val passwordEncoderService = mockk<PasswordEncoderService>()
        coEvery { repository.update(id, any()) } returns true
        coEvery { repository.get(id, id) } returns user.copy(biography = "Hi")

        val result = UpdateUserUseCaseImpl(repository, passwordEncoderService)(id, UpdateUserPayload(biography = "Hi"))

        assertEquals("Hi", result?.biography)
        coVerify { repository.get(id, id) }
    }

    @Test
    fun testHashesANewPassword() = runTest {
        val repository = mockk<UsersRepository>()
        val passwordEncoderService = mockk<PasswordEncoderService>()
        val written = slot<UpdateUserPayload>()
        every { passwordEncoderService.encode("hunter2") } returns "hashed"
        coEvery { repository.update(id, capture(written)) } returns true
        coEvery { repository.get(id, id) } returns user

        UpdateUserUseCaseImpl(repository, passwordEncoderService)(id, UpdateUserPayload(password = "hunter2"))

        assertEquals("hashed", written.captured.password)
    }

    @Test
    fun testRefusesAUsernameSomeoneElseHas() = runTest {
        val repository = mockk<UsersRepository>()
        val passwordEncoderService = mockk<PasswordEncoderService>()
        coEvery { repository.getForUsernameOrEmail("taken", false) } returns user.copy(id = otherId)

        assertFailsWith<UsernameAlreadyTakenException> {
            UpdateUserUseCaseImpl(repository, passwordEncoderService)(id, UpdateUserPayload(username = "taken"))
        }
    }

    /**
     * Sending back the username you already have is not a clash with yourself.
     */
    @Test
    fun testKeepingYourOwnUsernameIsFine() = runTest {
        val repository = mockk<UsersRepository>()
        val passwordEncoderService = mockk<PasswordEncoderService>()
        coEvery { repository.getForUsernameOrEmail("nathanfallet", false) } returns user
        coEvery { repository.update(id, any()) } returns true
        coEvery { repository.get(id, id) } returns user

        val result = UpdateUserUseCaseImpl(repository, passwordEncoderService)(
            id,
            UpdateUserPayload(username = "nathanfallet")
        )

        assertEquals(user, result)
    }

    /**
     * The keys carry the `users_` prefix the message bundle uses for this form, not the `auth_` one
     * of the sign-up form.
     */
    @Test
    fun testValidationKeysArePrefixedForThisForm() = runTest {
        val repository = mockk<UsersRepository>()
        val passwordEncoderService = mockk<PasswordEncoderService>()

        val exception = assertFailsWith<ValidationException> {
            UpdateUserUseCaseImpl(repository, passwordEncoderService)(id, UpdateUserPayload(username = "not valid"))
        }

        assertEquals("users_username_regex", exception.key)
    }

    @Test
    fun testAWriteThatTouchedNothingComesBackAsNothing() = runTest {
        val repository = mockk<UsersRepository>()
        val passwordEncoderService = mockk<PasswordEncoderService>()
        coEvery { repository.update(id, any()) } returns false

        val result = UpdateUserUseCaseImpl(repository, passwordEncoderService)(id, UpdateUserPayload(biography = "Hi"))

        assertNull(result)
    }

}

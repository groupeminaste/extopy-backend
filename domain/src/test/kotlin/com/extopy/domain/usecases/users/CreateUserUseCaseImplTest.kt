package com.extopy.domain.usecases.users

import com.extopy.domain.exceptions.auth.UsernameAlreadyTakenException
import com.extopy.domain.exceptions.validation.ValidationException
import com.extopy.domain.models.users.CreateUserPayload
import com.extopy.domain.models.users.User
import com.extopy.domain.repositories.UsersRepository
import com.extopy.domain.services.PasswordEncoderService
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid

class CreateUserUseCaseImplTest {

    private val payload = CreateUserPayload(
        username = "nathanfallet",
        displayName = "Nathan Fallet",
        email = "nathan@extopy.com",
        password = "hunter2",
        birthdate = LocalDate.parse("2002-12-24"),
    )

    private val user = User(
        id = Uuid.parse("00000000-0000-4000-8000-000000000001"),
        displayName = payload.displayName,
        username = payload.username,
    )

    private fun useCase(repository: UsersRepository, passwordEncoderService: PasswordEncoderService) =
        CreateUserUseCaseImpl(repository, passwordEncoderService)

    /**
     * The raw password must never reach the repository.
     */
    @Test
    fun testHashesThePassword() = runTest {
        val repository = mockk<UsersRepository>()
        val passwordEncoderService = mockk<PasswordEncoderService>()
        val written = slot<CreateUserPayload>()
        coEvery { repository.getForUsernameOrEmail(any(), false) } returns null
        every { passwordEncoderService.encode("hunter2") } returns "hashed"
        coEvery { repository.create(capture(written)) } returns user

        useCase(repository, passwordEncoderService)(payload)

        assertEquals("hashed", written.captured.password)
    }

    @Test
    fun testRefusesATakenUsername() = runTest {
        val repository = mockk<UsersRepository>()
        val passwordEncoderService = mockk<PasswordEncoderService>()
        coEvery { repository.getForUsernameOrEmail("nathanfallet", false) } returns user

        assertFailsWith<UsernameAlreadyTakenException> {
            useCase(repository, passwordEncoderService)(payload)
        }
    }

    /**
     * The keys are the ones the sign-up form already reads, so the page keeps naming these the same
     * way it did when the framework validated the payload.
     */
    @Test
    fun testRefusesAUsernameWithTheWrongShape() = runTest {
        val repository = mockk<UsersRepository>()
        val passwordEncoderService = mockk<PasswordEncoderService>()

        val exception = assertFailsWith<ValidationException> {
            useCase(repository, passwordEncoderService)(payload.copy(username = "nathan fallet"))
        }

        assertEquals("auth_username_regex", exception.key)
    }

    @Test
    fun testRefusesAUsernameThatIsTooLong() = runTest {
        val repository = mockk<UsersRepository>()
        val passwordEncoderService = mockk<PasswordEncoderService>()

        val exception = assertFailsWith<ValidationException> {
            useCase(repository, passwordEncoderService)(payload.copy(username = "n".repeat(26)))
        }

        assertEquals("auth_username_maxLength", exception.key)
    }

    @Test
    fun testRefusesADisplayNameThatIsTooLong() = runTest {
        val repository = mockk<UsersRepository>()
        val passwordEncoderService = mockk<PasswordEncoderService>()

        val exception = assertFailsWith<ValidationException> {
            useCase(repository, passwordEncoderService)(payload.copy(displayName = "N".repeat(41)))
        }

        assertEquals("auth_displayName_maxLength", exception.key)
    }

}

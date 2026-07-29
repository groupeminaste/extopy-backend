package com.extopy.domain.usecases.auth

import com.extopy.domain.models.auth.LoginPayload
import com.extopy.domain.models.users.User
import com.extopy.domain.repositories.UsersRepository
import com.extopy.domain.services.PasswordEncoderService
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.uuid.Uuid

class LoginUseCaseImplTest {

    private val user = User(
        id = Uuid.parse("00000000-0000-4000-8000-000000000001"),
        displayName = "Nathan Fallet",
        username = "nathanfallet",
        password = "hashed",
    )

    @Test
    fun testSignsInWithTheRightPassword() = runTest {
        val repository = mockk<UsersRepository>()
        val passwordEncoderService = mockk<PasswordEncoderService>()
        coEvery { repository.getForUsernameOrEmail("nathanfallet", true) } returns user
        every { passwordEncoderService.matches("hunter2", "hashed") } returns true

        val result = LoginUseCaseImpl(repository, passwordEncoderService)(
            LoginPayload("nathanfallet", "hunter2")
        )

        assertEquals(user.id, result?.id)
    }

    /**
     * The hash is read to check the password and must not travel any further than that.
     */
    @Test
    fun testTheHashDoesNotComeBack() = runTest {
        val repository = mockk<UsersRepository>()
        val passwordEncoderService = mockk<PasswordEncoderService>()
        coEvery { repository.getForUsernameOrEmail(any(), true) } returns user
        every { passwordEncoderService.matches(any(), any()) } returns true

        val result = LoginUseCaseImpl(repository, passwordEncoderService)(
            LoginPayload("nathanfallet", "hunter2")
        )

        assertNull(result?.password)
    }

    @Test
    fun testRefusesTheWrongPassword() = runTest {
        val repository = mockk<UsersRepository>()
        val passwordEncoderService = mockk<PasswordEncoderService>()
        coEvery { repository.getForUsernameOrEmail(any(), true) } returns user
        every { passwordEncoderService.matches(any(), any()) } returns false

        val result = LoginUseCaseImpl(repository, passwordEncoderService)(
            LoginPayload("nathanfallet", "wrong")
        )

        assertNull(result)
    }

    @Test
    fun testRefusesAnUnknownAccount() = runTest {
        val repository = mockk<UsersRepository>()
        val passwordEncoderService = mockk<PasswordEncoderService>()
        coEvery { repository.getForUsernameOrEmail(any(), true) } returns null

        val result = LoginUseCaseImpl(repository, passwordEncoderService)(
            LoginPayload("nobody", "hunter2")
        )

        assertNull(result)
    }

    /**
     * An account whose row carries no hash — one imported without a password, say — must not be a
     * way in for anyone sending an empty one.
     */
    @Test
    fun testAnAccountWithNoHashIsCheckedAgainstAnEmptyOne() = runTest {
        val repository = mockk<UsersRepository>()
        val passwordEncoderService = mockk<PasswordEncoderService>()
        coEvery { repository.getForUsernameOrEmail(any(), true) } returns user.copy(password = null)
        every { passwordEncoderService.matches("", "") } returns false
        every { passwordEncoderService.matches(any(), "") } returns false

        val result = LoginUseCaseImpl(repository, passwordEncoderService)(LoginPayload("nathanfallet", ""))

        assertNull(result)
    }

}

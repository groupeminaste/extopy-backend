package me.nathanfallet.extopy.usecases.auth

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.nathanfallet.extopy.models.auth.LoginPayload
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.extopy.repositories.users.IUsersRepository
import kotlin.test.Test
import kotlin.test.assertEquals

class LoginUseCaseTest {

    @Test
    fun invoke() = runBlocking {
        val repository = mockk<IUsersRepository>()
        val verifyPasswordUseCase = mockk<IVerifyPasswordUseCase>()
        val useCase = LoginUseCase(repository, verifyPasswordUseCase)
        val user = User("id", "displayName", "username", "email", "hash")
        coEvery { repository.getForEmail("email", true) } returns user
        every { verifyPasswordUseCase("password", "hash") } returns true
        assertEquals(user, useCase(LoginPayload("email", "password")))
    }

    @Test
    fun invokeNoUser() = runBlocking {
        val repository = mockk<IUsersRepository>()
        val verifyPasswordUseCase = mockk<IVerifyPasswordUseCase>()
        val useCase = LoginUseCase(repository, verifyPasswordUseCase)
        coEvery { repository.getForEmail("email", true) } returns null
        assertEquals(null, useCase(LoginPayload("email", "password")))
    }

    @Test
    fun invokeBadPassword() = runBlocking {
        val repository = mockk<IUsersRepository>()
        val verifyPasswordUseCase = mockk<IVerifyPasswordUseCase>()
        val useCase = LoginUseCase(repository, verifyPasswordUseCase)
        val user = User("id", "displayName", "username", "email", "hash")
        coEvery { repository.getForEmail("email", true) } returns user
        every { verifyPasswordUseCase("password", "hash") } returns false
        assertEquals(null, useCase(LoginPayload("email", "password")))
    }

}

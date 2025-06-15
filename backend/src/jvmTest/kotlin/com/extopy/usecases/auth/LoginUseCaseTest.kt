package com.extopy.usecases.auth

import com.extopy.models.auth.LoginPayload
import com.extopy.models.users.User
import com.extopy.database.users.IUsersRepository
import dev.kaccelero.commons.auth.IVerifyPasswordUseCase
import dev.kaccelero.commons.auth.VerifyPasswordPayload
import dev.kaccelero.models.UUID
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class LoginUseCaseTest {

    @Test
    fun invoke() = runBlocking {
        val repository = mockk<IUsersRepository>()
        val verifyPasswordUseCase = mockk<IVerifyPasswordUseCase>()
        val useCase = LoginUseCase(repository, verifyPasswordUseCase)
        val user = User(UUID(), "displayName", "username", "email", "hash")
        coEvery { repository.getForUsernameOrEmail("email", true) } returns user
        every { verifyPasswordUseCase(VerifyPasswordPayload("password", "hash")) } returns true
        assertEquals(user, useCase(LoginPayload("email", "password")))
    }

    @Test
    fun invokeNoUser() = runBlocking {
        val repository = mockk<IUsersRepository>()
        val verifyPasswordUseCase = mockk<IVerifyPasswordUseCase>()
        val useCase = LoginUseCase(repository, verifyPasswordUseCase)
        coEvery { repository.getForUsernameOrEmail("email", true) } returns null
        assertEquals(null, useCase(LoginPayload("email", "password")))
    }

    @Test
    fun invokeBadPassword() = runBlocking {
        val repository = mockk<IUsersRepository>()
        val verifyPasswordUseCase = mockk<IVerifyPasswordUseCase>()
        val useCase = LoginUseCase(repository, verifyPasswordUseCase)
        val user = User(UUID(), "displayName", "username", "email", "hash")
        coEvery { repository.getForUsernameOrEmail("email", true) } returns user
        every { verifyPasswordUseCase(VerifyPasswordPayload("password", "hash")) } returns false
        assertEquals(null, useCase(LoginPayload("email", "password")))
    }

}

package com.extopy.usecases.users

import com.extopy.models.users.UpdateUserPayload
import com.extopy.models.users.User
import com.extopy.repositories.users.IUsersRepository
import dev.kaccelero.commons.auth.IHashPasswordUseCase
import dev.kaccelero.commons.exceptions.ControllerException
import dev.kaccelero.models.UUID
import io.ktor.http.*
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UpdateUserUseCaseTest {

    @Test
    fun invoke() = runBlocking {
        val usersRepository = mockk<IUsersRepository>()
        val useCase = UpdateUserUseCase(usersRepository, mockk())
        val user = User(UUID(), "displayName", "username")
        val payload = UpdateUserPayload("newUsername", "newDisplayName")
        coEvery { usersRepository.getForUsernameOrEmail("newUsername", false) } returns null
        coEvery { usersRepository.update(user.id, payload) } returns true
        coEvery { usersRepository.get(user.id) } returns user
        assertEquals(user, useCase(user.id, payload))
    }

    @Test
    fun invokeKeepUsername() = runBlocking {
        val usersRepository = mockk<IUsersRepository>()
        val useCase = UpdateUserUseCase(usersRepository, mockk())
        val user = User(UUID(), "displayName", "username")
        val payload = UpdateUserPayload("username", "newDisplayName")
        coEvery { usersRepository.getForUsernameOrEmail("username", false) } returns user
        coEvery { usersRepository.update(user.id, payload) } returns true
        coEvery { usersRepository.get(user.id) } returns user
        assertEquals(user, useCase(user.id, payload))
    }

    @Test
    fun invokeWithPassword() = runBlocking {
        val usersRepository = mockk<IUsersRepository>()
        val hashPasswordUseCase = mockk<IHashPasswordUseCase>()
        val useCase = UpdateUserUseCase(usersRepository, hashPasswordUseCase)
        val user = User(UUID(), "displayName", "username")
        val payload = UpdateUserPayload("newUsername", "newDisplayName", "password")
        val hashedUser = user.copy(password = "hash")
        val hashedPayload = payload.copy(password = "hash")
        every { hashPasswordUseCase("password") } returns "hash"
        coEvery { usersRepository.getForUsernameOrEmail("newUsername", false) } returns null
        coEvery { usersRepository.update(user.id, hashedPayload) } returns true
        coEvery { usersRepository.get(user.id) } returns hashedUser
        assertEquals(hashedUser, useCase(user.id, payload))
    }

    @Test
    fun invokeError() = runBlocking {
        val usersRepository = mockk<IUsersRepository>()
        val useCase = UpdateUserUseCase(usersRepository, mockk())
        val user = User(UUID(), "displayName", "username")
        val payload = UpdateUserPayload("newUsername", "newDisplayName")
        coEvery { usersRepository.getForUsernameOrEmail("newUsername", false) } returns null
        coEvery { usersRepository.update(user.id, payload) } returns false
        assertEquals(null, useCase(user.id, payload))
    }

    @Test
    fun invokeStealUsername() = runBlocking {
        val usersRepository = mockk<IUsersRepository>()
        val useCase = UpdateUserUseCase(usersRepository, mockk())
        val user = User(UUID(), "displayName", "username")
        val otherUser = User(UUID(), "displayName", "newUsername")
        val payload = UpdateUserPayload("newUsername", "newDisplayName")
        coEvery { usersRepository.getForUsernameOrEmail("newUsername", false) } returns otherUser
        coEvery { usersRepository.update(user.id, payload) } returns true
        coEvery { usersRepository.get(user.id) } returns user
        val exception = assertFailsWith<ControllerException> {
            useCase(user.id, payload)
        }
        assertEquals(HttpStatusCode.BadRequest, exception.code)
        assertEquals("auth_register_username_taken", exception.key)
    }

}

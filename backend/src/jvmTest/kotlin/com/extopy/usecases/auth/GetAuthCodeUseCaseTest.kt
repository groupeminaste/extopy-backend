package com.extopy.usecases.auth

import com.extopy.models.application.Client
import com.extopy.models.auth.ClientForUser
import com.extopy.models.users.ClientInUser
import com.extopy.models.users.User
import com.extopy.models.users.UserContext
import com.extopy.database.users.IClientsInUsersRepository
import dev.kaccelero.commons.repositories.IGetModelSuspendUseCase
import dev.kaccelero.commons.repositories.IGetModelWithContextSuspendUseCase
import dev.kaccelero.models.UUID
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.*
import kotlin.test.Test
import kotlin.test.assertEquals

class GetAuthCodeUseCaseTest {

    private val now = Clock.System.now()
    private val tomorrow = now.plus(1, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
    private val yesterday = now.minus(1, DateTimeUnit.DAY, TimeZone.currentSystemDefault())

    @Test
    fun testInvoke() = runBlocking {
        val repository = mockk<IClientsInUsersRepository>()
        val getClientUseCase = mockk<IGetModelSuspendUseCase<Client, UUID>>()
        val getUserUseCase = mockk<IGetModelWithContextSuspendUseCase<User, UUID>>()
        val useCase = GetAuthCodeUseCase(repository, getClientUseCase, getUserUseCase)
        val clientForUser = ClientForUser(
            Client(UUID(), UUID(), "name", "description", "secret", "redirect"),
            User(UUID(), "displayName", "username")
        )
        coEvery { repository.get("code") } returns ClientInUser(
            "code", clientForUser.user.id, clientForUser.client.id, tomorrow
        )
        coEvery { getClientUseCase(clientForUser.client.id) } returns clientForUser.client
        coEvery { getUserUseCase(clientForUser.user.id, UserContext(clientForUser.user.id)) } returns clientForUser.user
        assertEquals(clientForUser, useCase("code"))
    }

    @Test
    fun testInvokeNotFound() = runBlocking {
        val repository = mockk<IClientsInUsersRepository>()
        val useCase = GetAuthCodeUseCase(repository, mockk(), mockk())
        coEvery { repository.get("code") } returns null
        assertEquals(null, useCase("code"))
    }

    @Test
    fun testInvokeExpired() = runBlocking {
        val repository = mockk<IClientsInUsersRepository>()
        val useCase = GetAuthCodeUseCase(repository, mockk(), mockk())
        coEvery { repository.get("code") } returns ClientInUser("code", UUID(), UUID(), yesterday)
        assertEquals(null, useCase("code"))
    }

    @Test
    fun testInvokeBadClient() = runBlocking {
        val repository = mockk<IClientsInUsersRepository>()
        val getClientUseCase = mockk<IGetModelSuspendUseCase<Client, UUID>>()
        val useCase = GetAuthCodeUseCase(repository, getClientUseCase, mockk())
        val clientId = UUID()
        coEvery { repository.get("code") } returns ClientInUser("code", UUID(), clientId, tomorrow)
        coEvery { getClientUseCase(clientId) } returns null
        assertEquals(null, useCase("code"))
    }

    @Test
    fun testInvokeBadUser() = runBlocking {
        val repository = mockk<IClientsInUsersRepository>()
        val getClientUseCase = mockk<IGetModelSuspendUseCase<Client, UUID>>()
        val getUserUseCase = mockk<IGetModelWithContextSuspendUseCase<User, UUID>>()
        val useCase = GetAuthCodeUseCase(repository, getClientUseCase, getUserUseCase)
        val clientForUser = ClientForUser(
            Client(UUID(), UUID(), "name", "description", "secret", "redirect"),
            User(UUID(), "displayName", "username")
        )
        coEvery { repository.get("code") } returns ClientInUser(
            "code", clientForUser.user.id, clientForUser.client.id, tomorrow
        )
        coEvery { getClientUseCase(clientForUser.client.id) } returns clientForUser.client
        coEvery { getUserUseCase(clientForUser.user.id, UserContext(clientForUser.user.id)) } returns null
        assertEquals(null, useCase("code"))
    }

}

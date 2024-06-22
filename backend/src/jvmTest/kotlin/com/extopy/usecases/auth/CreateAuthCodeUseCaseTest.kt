package com.extopy.usecases.auth

import com.extopy.models.application.Client
import com.extopy.models.auth.ClientForUser
import com.extopy.models.users.ClientInUser
import com.extopy.models.users.User
import com.extopy.repositories.users.IClientsInUsersRepository
import dev.kaccelero.models.UUID
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals

class CreateAuthCodeUseCaseTest {

    @Test
    fun testInvoke() = runBlocking {
        val repository = mockk<IClientsInUsersRepository>()
        val useCase = CreateAuthCodeUseCase(repository)
        val clientId = UUID()
        val userId = UUID()
        coEvery { repository.create(userId, clientId, any()) } returns ClientInUser(
            "code", userId, clientId, Clock.System.now()
        )
        assertEquals(
            "code",
            useCase(
                ClientForUser(
                    Client(clientId, UUID(), "name", "description", "secret", "redirect"),
                    User(userId, "displayName", "username")
                )
            )
        )
    }

    @Test
    fun testInvokeError() = runBlocking {
        val repository = mockk<IClientsInUsersRepository>()
        val useCase = CreateAuthCodeUseCase(repository)
        val clientId = UUID()
        val userId = UUID()
        coEvery { repository.create(userId, clientId, any()) } returns null
        assertEquals(
            null,
            useCase(
                ClientForUser(
                    Client(clientId, UUID(), "name", "description", "secret", "redirect"),
                    User(userId, "displayName", "username")
                )
            )
        )
    }

}

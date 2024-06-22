package com.extopy.usecases.auth

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import com.extopy.models.application.Client
import com.extopy.models.auth.ClientForUser
import com.extopy.models.users.ClientInUser
import com.extopy.models.users.User
import com.extopy.repositories.users.IClientsInUsersRepository
import kotlin.test.Test
import kotlin.test.assertEquals

class CreateAuthCodeUseCaseTest {

    @Test
    fun testInvoke() = runBlocking {
        val repository = mockk<IClientsInUsersRepository>()
        val useCase = CreateAuthCodeUseCase(repository)
        coEvery { repository.create("uid", "cid", any()) } returns ClientInUser(
            "code", "uid", "cid", Clock.System.now()
        )
        assertEquals(
            "code",
            useCase(
                ClientForUser(
                    Client("cid", "oid", "name", "description", "secret", "redirect"),
                    User("uid", "displayName", "username")
                )
            )
        )
    }

    @Test
    fun testInvokeError() = runBlocking {
        val repository = mockk<IClientsInUsersRepository>()
        val useCase = CreateAuthCodeUseCase(repository)
        coEvery { repository.create("uid", "cid", any()) } returns null
        assertEquals(
            null,
            useCase(
                ClientForUser(
                    Client("cid", "oid", "name", "description", "secret", "redirect"),
                    User("uid", "displayName", "username")
                )
            )
        )
    }

}

package me.nathanfallet.extopy.usecases.auth

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.*
import me.nathanfallet.extopy.models.application.Client
import me.nathanfallet.extopy.models.users.ClientInUser
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.extopy.repositories.users.IClientsInUsersRepository
import me.nathanfallet.ktorx.models.auth.ClientForUser
import me.nathanfallet.ktorx.usecases.auth.IGetClientUseCase
import me.nathanfallet.usecases.models.get.context.IGetModelWithContextSuspendUseCase
import kotlin.test.Test
import kotlin.test.assertEquals

class GetAuthCodeUseCaseTest {

    private val now = Clock.System.now()
    private val tomorrow = now.plus(1, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
    private val yesterday = now.minus(1, DateTimeUnit.DAY, TimeZone.currentSystemDefault())

    @Test
    fun testInvoke() = runBlocking {
        val repository = mockk<IClientsInUsersRepository>()
        val getClientUseCase = mockk<IGetClientUseCase>()
        val getUserUseCase = mockk<IGetModelWithContextSuspendUseCase<User, String>>()
        val useCase = GetAuthCodeUseCase(repository, getClientUseCase, getUserUseCase)
        val clientForUser = ClientForUser(
            Client("cid", "oid", "name", "secret", "redirect"),
            User("uid", "displayName", "username")
        )
        coEvery { repository.get("code") } returns ClientInUser("code", "uid", "cid", tomorrow)
        coEvery { getClientUseCase("cid") } returns clientForUser.client
        coEvery { getUserUseCase("uid", UserContext("uid")) } returns clientForUser.user as User
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
        coEvery { repository.get("code") } returns ClientInUser("code", "uid", "cid", yesterday)
        assertEquals(null, useCase("code"))
    }

    @Test
    fun testInvokeBadClient() = runBlocking {
        val repository = mockk<IClientsInUsersRepository>()
        val getClientUseCase = mockk<IGetClientUseCase>()
        val useCase = GetAuthCodeUseCase(repository, getClientUseCase, mockk())
        coEvery { repository.get("code") } returns ClientInUser("code", "uid", "cid", tomorrow)
        coEvery { getClientUseCase("cid") } returns null
        assertEquals(null, useCase("code"))
    }

    @Test
    fun testInvokeBadUser() = runBlocking {
        val repository = mockk<IClientsInUsersRepository>()
        val getClientUseCase = mockk<IGetClientUseCase>()
        val getUserUseCase = mockk<IGetModelWithContextSuspendUseCase<User, String>>()
        val useCase = GetAuthCodeUseCase(repository, getClientUseCase, getUserUseCase)
        val clientForUser = ClientForUser(
            Client("cid", "oid", "name", "secret", "redirect"),
            User("uid", "displayName", "username")
        )
        coEvery { repository.get("code") } returns ClientInUser("code", "uid", "cid", tomorrow)
        coEvery { getClientUseCase("cid") } returns clientForUser.client
        coEvery { getUserUseCase("uid", UserContext("uid")) } returns null
        assertEquals(null, useCase("code"))
    }

}

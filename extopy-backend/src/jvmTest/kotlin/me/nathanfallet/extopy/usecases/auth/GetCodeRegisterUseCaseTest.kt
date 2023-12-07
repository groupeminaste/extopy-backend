package me.nathanfallet.extopy.usecases.auth

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.*
import me.nathanfallet.extopy.models.application.CodeInEmail
import me.nathanfallet.extopy.models.auth.RegisterPayload
import me.nathanfallet.extopy.repositories.application.ICodesInEmailsRepository
import kotlin.test.Test
import kotlin.test.assertEquals

class GetCodeRegisterUseCaseTest {

    private val now = Clock.System.now()
    private val tomorrow = now.plus(1, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
    private val yesterday = now.minus(1, DateTimeUnit.DAY, TimeZone.currentSystemDefault())

    @Test
    fun invoke() = runBlocking {
        val repository = mockk<ICodesInEmailsRepository>()
        val useCase = GetCodeRegisterUseCase(repository)
        coEvery { repository.getCodeInEmail("code") } returns CodeInEmail("email", "code", tomorrow)
        assertEquals(RegisterPayload("email"), useCase(mockk(), "code"))
    }

    @Test
    fun invokeExpired() = runBlocking {
        val repository = mockk<ICodesInEmailsRepository>()
        val useCase = GetCodeRegisterUseCase(repository)
        coEvery { repository.getCodeInEmail("code") } returns CodeInEmail("email", "code", yesterday)
        assertEquals(null, useCase(mockk(), "code"))
    }

    @Test
    fun invokeNone() = runBlocking {
        val repository = mockk<ICodesInEmailsRepository>()
        val useCase = GetCodeRegisterUseCase(repository)
        coEvery { repository.getCodeInEmail("code") } returns null
        assertEquals(null, useCase(mockk(), "code"))
    }

}

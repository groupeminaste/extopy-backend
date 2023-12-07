package me.nathanfallet.extopy.usecases.auth

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.nathanfallet.extopy.repositories.application.ICodesInEmailsRepository
import kotlin.test.Test

class DeleteCodeRegisterUseCaseTest {

    @Test
    fun testInvoke() = runBlocking {
        val repository = mockk<ICodesInEmailsRepository>()
        val useCase = DeleteCodeRegisterUseCase(repository)
        coEvery { repository.deleteCodeInEmail("code") } returns Unit
        useCase(mockk(), "code")
        coVerify { repository.deleteCodeInEmail("code") }
    }

}

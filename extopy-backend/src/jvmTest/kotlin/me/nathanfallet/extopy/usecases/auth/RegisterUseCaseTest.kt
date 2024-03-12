package me.nathanfallet.extopy.usecases.auth

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import me.nathanfallet.extopy.models.application.CodeInEmail
import me.nathanfallet.extopy.models.auth.RegisterCodePayload
import me.nathanfallet.extopy.models.users.CreateUserPayload
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.extopy.usecases.application.IGetCodeInEmailUseCase
import me.nathanfallet.usecases.models.create.ICreateModelSuspendUseCase
import kotlin.test.Test
import kotlin.test.assertEquals

class RegisterUseCaseTest {

    @Test
    fun testRegisterCodePayload() = runBlocking {
        val getCodeInEmailUseCase = mockk<IGetCodeInEmailUseCase>()
        val createUserUseCase = mockk<ICreateModelSuspendUseCase<User, CreateUserPayload>>()
        val useCase = RegisterUseCase(getCodeInEmailUseCase, createUserUseCase)
        val payload = CreateUserPayload(
            "username", "displayName", "email", "password",
            LocalDate(2002, 12, 24)
        )
        val user = User("id", "displayName", "username")
        coEvery { getCodeInEmailUseCase("code") } returns CodeInEmail("email", "code", Clock.System.now())
        coEvery { createUserUseCase(payload) } returns user
        assertEquals(
            user,
            useCase("code", RegisterCodePayload("password", "username", "displayName", LocalDate(2002, 12, 24)))
        )
    }

    @Test
    fun testRegisterCodePayloadNotExists() = runBlocking {
        val getCodeInEmailUseCase = mockk<IGetCodeInEmailUseCase>()
        val useCase = RegisterUseCase(getCodeInEmailUseCase, mockk())
        coEvery { getCodeInEmailUseCase("code") } returns null
        assertEquals(
            null,
            useCase("code", RegisterCodePayload("password", "username", "displayName", LocalDate(2002, 12, 24)))
        )
    }

}

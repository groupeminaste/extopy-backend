package com.extopy.usecases.auth

import com.extopy.models.application.CodeInEmail
import com.extopy.models.auth.RegisterCodePayload
import com.extopy.models.users.CreateUserPayload
import com.extopy.models.users.User
import com.extopy.usecases.application.IGetCodeInEmailUseCase
import dev.kaccelero.commons.repositories.ICreateModelSuspendUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
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

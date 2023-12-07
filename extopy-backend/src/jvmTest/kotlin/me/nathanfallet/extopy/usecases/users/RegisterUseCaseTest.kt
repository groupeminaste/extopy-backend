package me.nathanfallet.extopy.usecases.users

import io.ktor.server.application.*
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import me.nathanfallet.extopy.models.auth.RegisterCodePayload
import me.nathanfallet.extopy.models.auth.RegisterPayload
import me.nathanfallet.extopy.models.users.CreateUserPayload
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.extopy.usecases.auth.RegisterUseCase
import me.nathanfallet.ktorx.usecases.auth.IGetCodeRegisterUseCase
import me.nathanfallet.usecases.models.create.ICreateModelSuspendUseCase
import kotlin.test.Test
import kotlin.test.assertEquals

class RegisterUseCaseTest {

    @Test
    fun testRegisterCodePayload() = runBlocking {
        val getCodeRegisterUseCase = mockk<IGetCodeRegisterUseCase<RegisterPayload>>()
        val call = mockk<ApplicationCall>()
        val createUserUseCase = mockk<ICreateModelSuspendUseCase<User, CreateUserPayload>>()
        val useCase = RegisterUseCase(getCodeRegisterUseCase, createUserUseCase)
        val payload = CreateUserPayload(
            "username", "displayName", "email", "password",
            LocalDate(2002, 12, 24)
        )
        val user = User("id", "displayName", "username")
        every { call.parameters["code"] } returns "code"
        coEvery { getCodeRegisterUseCase(call, "code") } returns RegisterPayload("email")
        coEvery { createUserUseCase(payload) } returns user
        assertEquals(
            user,
            useCase(call, RegisterCodePayload("password", "username", "displayName", LocalDate(2002, 12, 24)))
        )
    }

    @Test
    fun testRegisterCodePayloadNotExists() = runBlocking {
        val getCodeRegisterUseCase = mockk<IGetCodeRegisterUseCase<RegisterPayload>>()
        val call = mockk<ApplicationCall>()
        val useCase = RegisterUseCase(getCodeRegisterUseCase, mockk())
        every { call.parameters["code"] } returns "code"
        coEvery { getCodeRegisterUseCase(call, "code") } returns null
        assertEquals(
            null,
            useCase(call, RegisterCodePayload("password", "username", "displayName", LocalDate(2002, 12, 24)))
        )
    }

}

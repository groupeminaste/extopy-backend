package me.nathanfallet.extopy.usecases.auth

import io.ktor.http.*
import io.ktor.server.application.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import me.nathanfallet.extopy.models.application.CodeInEmail
import me.nathanfallet.extopy.models.application.Email
import me.nathanfallet.extopy.models.auth.RegisterPayload
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.extopy.repositories.application.ICodesInEmailsRepository
import me.nathanfallet.extopy.repositories.users.IUsersRepository
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.ktorx.usecases.localization.IGetLocaleForCallUseCase
import me.nathanfallet.usecases.emails.ISendEmailUseCase
import me.nathanfallet.usecases.localization.ITranslateUseCase
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CreateCodeRegisterUseCaseTest {

    @Test
    fun testRegister() = runBlocking {
        val codesInEmailsRepository = mockk<ICodesInEmailsRepository>()
        val usersRepository = mockk<IUsersRepository>()
        val sendEmailUseCase = mockk<ISendEmailUseCase>()
        val translateUseCase = mockk<ITranslateUseCase>()
        val getLocaleForCallUseCase = mockk<IGetLocaleForCallUseCase>()
        val call = mockk<ApplicationCall>()
        val useCase = CreateCodeRegisterUseCase(
            codesInEmailsRepository, usersRepository,
            sendEmailUseCase, translateUseCase, getLocaleForCallUseCase
        )
        coEvery { usersRepository.getForUsernameOrEmail("email", false) } returns null
        coEvery { codesInEmailsRepository.createCodeInEmail("email", any(), any()) } returns CodeInEmail(
            "email", "code", Clock.System.now()
        )
        coEvery { sendEmailUseCase(any(), any()) } returns Unit
        every {
            translateUseCase(
                Locale.ENGLISH,
                any()
            )
        } answers { "t:${secondArg<String>()}" }
        every { getLocaleForCallUseCase(call) } returns Locale.ENGLISH
        every {
            translateUseCase(
                Locale.ENGLISH,
                any(),
                any()
            )
        } answers { "t:${secondArg<String>()}:${thirdArg<List<String>>()}" }
        assertEquals("code", useCase(call, RegisterPayload("email")))
        coVerify {
            sendEmailUseCase(
                Email(
                    "t:auth_register_email_title",
                    "t:auth_register_email_body:[code]"
                ),
                listOf("email")
            )
        }
    }

    @Test
    fun testRegisterCodeTaken() = runBlocking {
        val codesInEmailsRepository = mockk<ICodesInEmailsRepository>()
        val usersRepository = mockk<IUsersRepository>()
        val sendEmailUseCase = mockk<ISendEmailUseCase>()
        val translateUseCase = mockk<ITranslateUseCase>()
        val getLocaleForCallUseCase = mockk<IGetLocaleForCallUseCase>()
        val call = mockk<ApplicationCall>()
        val useCase = CreateCodeRegisterUseCase(
            codesInEmailsRepository, usersRepository,
            sendEmailUseCase, translateUseCase, getLocaleForCallUseCase
        )
        coEvery { usersRepository.getForUsernameOrEmail("email", false) } returns null
        coEvery { codesInEmailsRepository.createCodeInEmail("email", any(), any()) } throws Exception()
        coEvery { codesInEmailsRepository.updateCodeInEmail("email", any(), any()) } returns true
        coEvery { sendEmailUseCase(any(), any()) } returns Unit
        every {
            translateUseCase(
                Locale.ENGLISH,
                any()
            )
        } returns "t"
        every { getLocaleForCallUseCase(call) } returns Locale.ENGLISH
        every {
            translateUseCase(
                Locale.ENGLISH,
                any(),
                any()
            )
        } returns "t"
        useCase(call, RegisterPayload("email"))
        coVerify {
            codesInEmailsRepository.updateCodeInEmail("email", any(), any())
        }
    }

    @Test
    fun testRegisterInternalError() = runBlocking {
        val codesInEmailsRepository = mockk<ICodesInEmailsRepository>()
        val usersRepository = mockk<IUsersRepository>()
        val call = mockk<ApplicationCall>()
        val useCase = CreateCodeRegisterUseCase(
            codesInEmailsRepository, usersRepository,
            mockk(), mockk(), mockk()
        )
        coEvery { usersRepository.getForUsernameOrEmail("email", false) } returns null
        coEvery { codesInEmailsRepository.createCodeInEmail("email", any(), any()) } throws Exception()
        coEvery { codesInEmailsRepository.updateCodeInEmail("email", any(), any()) } returns false
        val exception = assertFailsWith<ControllerException> {
            useCase(call, RegisterPayload("email"))
        }
        assertEquals(HttpStatusCode.InternalServerError, exception.code)
        assertEquals("error_internal", exception.key)
    }

    @Test
    fun testRegisterEmailTaken() = runBlocking {
        val codesInEmailsRepository = mockk<ICodesInEmailsRepository>()
        val usersRepository = mockk<IUsersRepository>()
        val sendEmailUseCase = mockk<ISendEmailUseCase>()
        val translateUseCase = mockk<ITranslateUseCase>()
        val getLocaleForCallUseCase = mockk<IGetLocaleForCallUseCase>()
        val call = mockk<ApplicationCall>()
        val useCase = CreateCodeRegisterUseCase(
            codesInEmailsRepository, usersRepository,
            sendEmailUseCase, translateUseCase, getLocaleForCallUseCase
        )
        coEvery { usersRepository.getForUsernameOrEmail("email", false) } returns User(
            "id", "displayName", "email"
        )
        val exception = assertFailsWith<ControllerException> {
            useCase(call, RegisterPayload("email"))
        }
        assertEquals(HttpStatusCode.BadRequest, exception.code)
        assertEquals("auth_register_email_taken", exception.key)
    }

}

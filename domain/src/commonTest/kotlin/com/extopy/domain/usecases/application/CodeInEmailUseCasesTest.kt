package com.extopy.domain.usecases.application

import com.extopy.domain.exceptions.auth.EmailAlreadyTakenException
import com.extopy.domain.models.application.CodeInEmail
import com.extopy.domain.models.application.Email
import com.extopy.domain.models.users.User
import com.extopy.domain.repositories.CodesInEmailsRepository
import com.extopy.domain.repositories.UsersRepository
import com.extopy.domain.services.CodeGeneratorService
import com.extopy.domain.services.EmailsService
import com.extopy.domain.services.TranslateService
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.uuid.Uuid

class CodeInEmailUseCasesTest {

    private val user = User(
        id = Uuid.parse("00000000-0000-4000-8000-000000000001"),
        displayName = "Nathan Fallet",
        username = "nathanfallet",
    )

    @Test
    fun testDrawingACode() = runTest {
        val repository = mockk<CodesInEmailsRepository>()
        val usersRepository = mockk<UsersRepository>()
        val codeGeneratorService = mockk<CodeGeneratorService>()
        coEvery { usersRepository.getForUsernameOrEmail("someone@extopy.com", false) } returns null
        every { codeGeneratorService.generate() } returns "drawn"
        coEvery { repository.set("someone@extopy.com", "drawn", any()) } answers {
            CodeInEmail("someone@extopy.com", "drawn", thirdArg())
        }

        val result = CreateCodeInEmailUseCaseImpl(repository, usersRepository, codeGeneratorService)(
            "someone@extopy.com"
        )

        assertEquals("drawn", result?.code)
    }

    @Test
    fun testAnAddressThatAlreadyHasAnAccount() = runTest {
        val repository = mockk<CodesInEmailsRepository>()
        val usersRepository = mockk<UsersRepository>()
        val codeGeneratorService = mockk<CodeGeneratorService>()
        coEvery { usersRepository.getForUsernameOrEmail(any(), false) } returns user

        assertFailsWith<EmailAlreadyTakenException> {
            CreateCodeInEmailUseCaseImpl(repository, usersRepository, codeGeneratorService)("taken@extopy.com")
        }
    }

    @Test
    fun testACodeThatIsStillGood() = runTest {
        val repository = mockk<CodesInEmailsRepository>()
        val code = CodeInEmail("someone@extopy.com", "abc", Clock.System.now() + 1.hours)
        coEvery { repository.get("abc") } returns code

        assertEquals(code, GetCodeInEmailUseCaseImpl(repository)("abc"))
    }

    @Test
    fun testACodeThatSatAroundForTooLong() = runTest {
        val repository = mockk<CodesInEmailsRepository>()
        coEvery { repository.get("abc") } returns
                CodeInEmail("someone@extopy.com", "abc", Clock.System.now() - 1.hours)

        assertNull(GetCodeInEmailUseCaseImpl(repository)("abc"))
    }

    /**
     * The link the email carries points back at the deployment that sent it, and the message is
     * written in the language the visitor was reading.
     */
    @Test
    fun testTheRegistrationEmail() = runTest {
        val emailsService = mockk<EmailsService>()
        val translateService = mockk<TranslateService>()
        val sent = slot<Email>()
        every { translateService.translate("auth_register_email_title", "fr", any()) } returns "Inscription"
        every { translateService.translate("auth_register_email_body", "fr", any()) } answers {
            "Suivez ce lien : ${(thirdArg<List<String>>()).first()}"
        }
        coEvery { emailsService.send(capture(sent), listOf("someone@extopy.com")) } returns Unit

        SendRegistrationEmailUseCaseImpl(emailsService, translateService, "https://extopy.dev")(
            "someone@extopy.com",
            "abc",
            "fr"
        )

        assertEquals("Inscription", sent.captured.title)
        assertEquals("Suivez ce lien : https://extopy.dev/auth/register/abc", sent.captured.body)
    }

}

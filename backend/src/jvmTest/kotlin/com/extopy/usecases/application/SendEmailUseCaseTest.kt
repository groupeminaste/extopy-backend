package com.extopy.usecases.application

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import com.extopy.models.application.Email
import com.extopy.services.emails.IEmailsService
import kotlin.test.Test

class SendEmailUseCaseTest {

    @Test
    fun invoke() {
        val service = mockk<IEmailsService>()
        val useCase = SendEmailUseCase(service)
        val email = Email("subject", "content")
        every { service.sendEmail(email, listOf("email")) } returns Unit
        useCase(email, listOf("email"))
        verify { service.sendEmail(email, listOf("email")) }
    }

}

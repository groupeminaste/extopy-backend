package com.extopy.domain.usecases.application

import com.extopy.domain.models.application.Email
import com.extopy.domain.services.EmailsService
import com.extopy.domain.services.TranslateService

class SendRegistrationEmailUseCaseImpl(
    private val emailsService: EmailsService,
    private val translateService: TranslateService,
    private val baseUrl: String,
) : SendRegistrationEmailUseCase {

    override suspend fun invoke(email: String, code: String, language: String) = emailsService.send(
        Email(
            title = translateService.translate("auth_register_email_title", language),
            body = translateService.translate(
                "auth_register_email_body",
                language,
                listOf("$baseUrl/auth/register/$code")
            ),
        ),
        listOf(email)
    )

}

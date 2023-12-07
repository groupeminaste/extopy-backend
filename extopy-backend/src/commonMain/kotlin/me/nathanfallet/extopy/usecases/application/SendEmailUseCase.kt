package me.nathanfallet.extopy.usecases.application

import me.nathanfallet.extopy.services.emails.IEmailsService
import me.nathanfallet.usecases.emails.IEmail
import me.nathanfallet.usecases.emails.ISendEmailUseCase

class SendEmailUseCase(
    private val emailsService: IEmailsService,
) : ISendEmailUseCase {

    override fun invoke(input1: IEmail, input2: List<String>) {
        emailsService.sendEmail(input1, input2)
    }

}

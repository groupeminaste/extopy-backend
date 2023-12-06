package me.nathanfallet.extopy.usecases.auth

import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.datetime.Clock
import me.nathanfallet.extopy.models.application.CodeInEmail
import me.nathanfallet.extopy.models.application.Email
import me.nathanfallet.extopy.models.auth.RegisterPayload
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.ktorx.usecases.auth.ICreateCodeRegisterUseCase
import me.nathanfallet.ktorx.usecases.localization.IGetLocaleForCallUseCase
import me.nathanfallet.usecases.emails.ISendEmailUseCase
import me.nathanfallet.usecases.localization.ITranslateUseCase

class CreateCodeRegisterUseCase(
    private val sendEmailUseCase: ISendEmailUseCase,
    private val translateUseCase: ITranslateUseCase,
    private val getLocaleForCallUseCase: IGetLocaleForCallUseCase,
) : ICreateCodeRegisterUseCase<RegisterPayload> {

    override suspend fun invoke(input1: ApplicationCall, input2: RegisterPayload): String {
        // TODO: Real code generation
        val code = CodeInEmail(input2.email, "code", Clock.System.now()) ?: throw ControllerException(
            HttpStatusCode.BadRequest, "auth_register_email_taken"
        )
        val locale = getLocaleForCallUseCase(input1)
        sendEmailUseCase(
            Email(
                translateUseCase(locale, "auth_register_email_title"),
                translateUseCase(locale, "auth_register_email_body", listOf(code.code))
            ),
            listOf(input2.email)
        )
        return code.code
    }

}

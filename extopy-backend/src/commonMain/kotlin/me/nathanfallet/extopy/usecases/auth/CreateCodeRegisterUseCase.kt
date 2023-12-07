package me.nathanfallet.extopy.usecases.auth

import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import me.nathanfallet.extopy.extensions.generateId
import me.nathanfallet.extopy.models.application.CodeInEmail
import me.nathanfallet.extopy.models.application.Email
import me.nathanfallet.extopy.models.auth.RegisterPayload
import me.nathanfallet.extopy.repositories.application.ICodesInEmailsRepository
import me.nathanfallet.extopy.repositories.users.IUsersRepository
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.ktorx.usecases.auth.ICreateCodeRegisterUseCase
import me.nathanfallet.ktorx.usecases.localization.IGetLocaleForCallUseCase
import me.nathanfallet.usecases.emails.ISendEmailUseCase
import me.nathanfallet.usecases.localization.ITranslateUseCase

class CreateCodeRegisterUseCase(
    private val codesInEmailsRepository: ICodesInEmailsRepository,
    private val usersRepository: IUsersRepository,
    private val sendEmailUseCase: ISendEmailUseCase,
    private val translateUseCase: ITranslateUseCase,
    private val getLocaleForCallUseCase: IGetLocaleForCallUseCase,
) : ICreateCodeRegisterUseCase<RegisterPayload> {

    override suspend fun invoke(input1: ApplicationCall, input2: RegisterPayload): String {
        val code = generateCode(input2.email) ?: throw ControllerException(
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

    private suspend fun generateCode(email: String): CodeInEmail? {
        usersRepository.getForUsernameOrEmail(email, false)?.let {
            return null
        }
        val code = String.generateId()
        val expiresAt = Clock.System.now().plus(1, DateTimeUnit.HOUR, TimeZone.currentSystemDefault())
        return try {
            codesInEmailsRepository.createCodeInEmail(email, code, expiresAt)
        } catch (e: Exception) {
            codesInEmailsRepository.updateCodeInEmail(email, code, expiresAt).takeIf {
                it
            } ?: throw ControllerException(HttpStatusCode.InternalServerError, "error_internal")
            CodeInEmail(email, code, expiresAt)
        }
    }

}

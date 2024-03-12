package me.nathanfallet.extopy.usecases.application

import io.ktor.http.*
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import me.nathanfallet.extopy.extensions.generateId
import me.nathanfallet.extopy.models.application.CodeInEmail
import me.nathanfallet.extopy.repositories.application.ICodesInEmailsRepository
import me.nathanfallet.extopy.repositories.users.IUsersRepository
import me.nathanfallet.ktorx.models.exceptions.ControllerException

class CreateCodeInEmailUseCase(
    private val codesInEmailsRepository: ICodesInEmailsRepository,
    private val usersRepository: IUsersRepository,
) : ICreateCodeInEmailUseCase {

    override suspend fun invoke(input: String): CodeInEmail? {
        usersRepository.getForUsernameOrEmail(input, false)?.let {
            return null
        }
        val code = String.generateId()
        val expiresAt = Clock.System.now().plus(1, DateTimeUnit.HOUR, TimeZone.currentSystemDefault())
        return try {
            codesInEmailsRepository.createCodeInEmail(input, code, expiresAt)
        } catch (e: Exception) {
            codesInEmailsRepository.updateCodeInEmail(input, code, expiresAt).takeIf {
                it
            } ?: throw ControllerException(HttpStatusCode.InternalServerError, "error_internal")
            CodeInEmail(input, code, expiresAt)
        }
    }

}

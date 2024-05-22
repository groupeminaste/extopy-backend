package me.nathanfallet.extopy.controllers.auth

import io.ktor.http.*
import io.ktor.server.application.*
import me.nathanfallet.extopy.models.application.Client
import me.nathanfallet.extopy.models.application.Email
import me.nathanfallet.extopy.models.auth.*
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.extopy.usecases.application.ICreateCodeInEmailUseCase
import me.nathanfallet.extopy.usecases.application.IDeleteCodeInEmailUseCase
import me.nathanfallet.extopy.usecases.application.IGetClientForCallUseCase
import me.nathanfallet.extopy.usecases.application.IGetCodeInEmailUseCase
import me.nathanfallet.extopy.usecases.auth.*
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.ktorx.models.responses.RedirectResponse
import me.nathanfallet.ktorx.usecases.localization.IGetLocaleForCallUseCase
import me.nathanfallet.ktorx.usecases.users.IRequireUserForCallUseCase
import me.nathanfallet.usecases.auth.AuthRequest
import me.nathanfallet.usecases.auth.AuthToken
import me.nathanfallet.usecases.emails.ISendEmailUseCase
import me.nathanfallet.usecases.localization.ITranslateUseCase
import me.nathanfallet.usecases.models.get.IGetModelSuspendUseCase

class AuthController(
    private val loginUseCase: ILoginUseCase,
    private val registerUseCase: IRegisterUseCase,
    private val setSessionForCallUseCase: ISetSessionForCallUseCase,
    private val clearSessionForCallUseCase: IClearSessionForCallUseCase,
    private val requireUserForCallUseCase: IRequireUserForCallUseCase,
    private val getClientUseCase: IGetModelSuspendUseCase<Client, String>,
    private val getClientForCallUseCase: IGetClientForCallUseCase,
    private val getAuthCodeUseCase: IGetAuthCodeUseCase,
    private val createAuthCodeUseCase: ICreateAuthCodeUseCase,
    private val deleteAuthCodeUseCase: IDeleteAuthCodeUseCase,
    private val generateAuthTokenUseCase: IGenerateAuthTokenUseCase,
    private val createCodeInEmailUseCase: ICreateCodeInEmailUseCase,
    private val getCodeInEmailUseCase: IGetCodeInEmailUseCase,
    private val deleteCodeInEmailUseCase: IDeleteCodeInEmailUseCase,
    private val sendEmailUseCase: ISendEmailUseCase,
    private val getLocaleForCallUseCase: IGetLocaleForCallUseCase,
    private val translateUseCase: ITranslateUseCase,
) : IAuthController {

    override fun login() {}

    override suspend fun login(call: ApplicationCall, payload: LoginPayload, redirect: String?): RedirectResponse {
        val user = loginUseCase(payload) ?: throw ControllerException(
            HttpStatusCode.Unauthorized, "auth_invalid_credentials"
        )
        setSessionForCallUseCase(call, SessionPayload(user.id))
        return RedirectResponse(redirect ?: "/")
    }

    override suspend fun logout(call: ApplicationCall, redirect: String?): RedirectResponse {
        clearSessionForCallUseCase(call)
        return RedirectResponse(redirect ?: "/")
    }

    override fun register() {}

    override suspend fun register(call: ApplicationCall, payload: RegisterPayload): Map<String, Any> {
        val code = createCodeInEmailUseCase(payload.email) ?: throw ControllerException(
            HttpStatusCode.BadRequest, "auth_register_email_taken"
        )
        val locale = getLocaleForCallUseCase(call)
        sendEmailUseCase(
            Email(
                translateUseCase(locale, "auth_register_email_title"),
                translateUseCase(locale, "auth_register_email_body", listOf(code.code))
            ),
            listOf(payload.email)
        )
        return mapOf("success" to "auth_register_code_created")
    }

    override suspend fun registerCode(call: ApplicationCall, code: String): RegisterPayload {
        val codeInEmail = getCodeInEmailUseCase(code) ?: throw ControllerException(
            HttpStatusCode.NotFound, "auth_code_invalid"
        )
        return RegisterPayload(codeInEmail.email)
    }

    override suspend fun registerCode(
        call: ApplicationCall,
        code: String,
        payload: RegisterCodePayload,
        redirect: String?,
    ): RedirectResponse {
        val user = registerUseCase(code, payload) ?: throw ControllerException(
            HttpStatusCode.InternalServerError, "error_internal"
        )
        setSessionForCallUseCase(call, SessionPayload(user.id))
        deleteCodeInEmailUseCase(code)
        return RedirectResponse(redirect ?: "/")
    }

    override suspend fun authorize(call: ApplicationCall, clientId: String?): ClientForUser {
        val user = requireUserForCallUseCase(call)
        val client = clientId?.let { getClientUseCase(it) } ?: throw ControllerException(
            HttpStatusCode.BadRequest, "auth_invalid_client"
        )
        return ClientForUser(client, user as User)
    }

    override suspend fun authorizeRedirect(call: ApplicationCall, clientId: String?): Map<String, Any> {
        val client = authorize(call, clientId)
        val code = createAuthCodeUseCase(client) ?: throw ControllerException(
            HttpStatusCode.InternalServerError, "error_internal"
        )
        return mapOf(
            "redirect" to client.client.redirectUri.replace("{code}", code)
        )
    }

    override suspend fun token(payload: AuthRequest): AuthToken {
        val client = getAuthCodeUseCase(payload.code)?.takeIf {
            it.client.clientId == payload.clientId && it.client.clientSecret == payload.clientSecret
        } ?: throw ControllerException(
            HttpStatusCode.BadRequest, "auth_invalid_code"
        )
        if (!deleteAuthCodeUseCase(payload.code)) throw ControllerException(
            HttpStatusCode.InternalServerError, "error_internal"
        )
        return generateAuthTokenUseCase(client)
    }

    override suspend fun refreshToken(call: ApplicationCall): AuthToken {
        val user = requireUserForCallUseCase(call) as? User
        val client = getClientForCallUseCase(call) as? Client
        if (user == null || client == null) throw ControllerException(
            HttpStatusCode.BadRequest, "auth_invalid_code"
        )
        return generateAuthTokenUseCase(ClientForUser(client, user))
    }

}

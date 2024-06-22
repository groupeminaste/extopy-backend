package com.extopy.controllers.auth

import com.extopy.models.application.Client
import com.extopy.models.application.Email
import com.extopy.models.auth.*
import com.extopy.models.users.User
import com.extopy.usecases.application.ICreateCodeInEmailUseCase
import com.extopy.usecases.application.IDeleteCodeInEmailUseCase
import com.extopy.usecases.application.IGetCodeInEmailUseCase
import com.extopy.usecases.auth.*
import dev.kaccelero.commons.emails.ISendEmailUseCase
import dev.kaccelero.commons.exceptions.ControllerException
import dev.kaccelero.commons.localization.IGetLocaleForCallUseCase
import dev.kaccelero.commons.localization.ITranslateUseCase
import dev.kaccelero.commons.repositories.IGetModelSuspendUseCase
import dev.kaccelero.commons.responses.RedirectResponse
import dev.kaccelero.commons.users.IRequireUserForCallUseCase
import io.ktor.http.*
import io.ktor.server.application.*

class AuthController(
    private val loginUseCase: ILoginUseCase,
    private val registerUseCase: IRegisterUseCase,
    private val setSessionForCallUseCase: ISetSessionForCallUseCase,
    private val clearSessionForCallUseCase: IClearSessionForCallUseCase,
    private val requireUserForCallUseCase: IRequireUserForCallUseCase,
    private val getClientUseCase: IGetModelSuspendUseCase<Client, String>,
    private val getClientForUserForRefreshTokenUseCase: IGetClientForUserForRefreshTokenUseCase,
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
            it.client.clientId == payload.clientId.toString() && it.client.clientSecret == payload.clientSecret
        } ?: throw ControllerException(
            HttpStatusCode.BadRequest, "auth_invalid_code"
        )
        if (!deleteAuthCodeUseCase(payload.code)) throw ControllerException(
            HttpStatusCode.InternalServerError, "error_internal"
        )
        return generateAuthTokenUseCase(client)
    }

    override suspend fun refreshToken(payload: RefreshTokenPayload): AuthToken {
        val client = getClientForUserForRefreshTokenUseCase(payload.refreshToken) ?: throw ControllerException(
            HttpStatusCode.BadRequest, "auth_invalid_code"
        )
        return generateAuthTokenUseCase(client)
    }

}

package me.nathanfallet.extopy.controllers.auth

import io.ktor.server.application.*
import me.nathanfallet.extopy.models.auth.LoginPayload
import me.nathanfallet.extopy.models.auth.RegisterCodePayload
import me.nathanfallet.extopy.models.auth.RegisterPayload
import me.nathanfallet.ktorx.controllers.auth.AbstractAuthWithCodeController
import me.nathanfallet.ktorx.models.annotations.*
import me.nathanfallet.ktorx.models.auth.ClientForUser
import me.nathanfallet.ktorx.usecases.auth.*
import me.nathanfallet.ktorx.usecases.users.IRequireUserForCallUseCase
import me.nathanfallet.usecases.auth.AuthRequest
import me.nathanfallet.usecases.auth.AuthToken

class AuthController(
    loginUseCase: ILoginUseCase<LoginPayload>,
    registerUseCase: IRegisterUseCase<RegisterCodePayload>,
    createSessionForUserUseCase: ICreateSessionForUserUseCase,
    setSessionForCallUseCase: ISetSessionForCallUseCase,
    createCodeRegisterUseCase: ICreateCodeRegisterUseCase<RegisterPayload>,
    getCodeRegisterUseCase: IGetCodeRegisterUseCase<RegisterPayload>,
    deleteCodeRegisterUseCase: IDeleteCodeRegisterUseCase,
    requireUserForCallUseCase: IRequireUserForCallUseCase,
    getClientUseCase: IGetClientUseCase,
    getAuthCodeUseCase: IGetAuthCodeUseCase,
    createAuthCodeUseCase: ICreateAuthCodeUseCase,
    deleteAuthCodeUseCase: IDeleteAuthCodeUseCase,
    generateAuthTokenUseCase: IGenerateAuthTokenUseCase,
) : AbstractAuthWithCodeController<LoginPayload, RegisterPayload, RegisterCodePayload>(
    loginUseCase,
    registerUseCase,
    createSessionForUserUseCase,
    setSessionForCallUseCase,
    createCodeRegisterUseCase,
    getCodeRegisterUseCase,
    deleteCodeRegisterUseCase,
    requireUserForCallUseCase,
    getClientUseCase,
    getAuthCodeUseCase,
    createAuthCodeUseCase,
    deleteAuthCodeUseCase,
    generateAuthTokenUseCase,
) {

    @TemplateMapping("auth/login.ftl")
    @LoginPath
    override suspend fun login(call: ApplicationCall, @Payload payload: LoginPayload) {
        super.login(call, payload)
    }

    @TemplateMapping("auth/register.ftl")
    @RegisterPath
    override suspend fun register(call: ApplicationCall, @Payload payload: RegisterPayload) {
        super.register(call, payload)
    }

    @TemplateMapping("auth/register.ftl")
    @RegisterCodePath
    override suspend fun register(call: ApplicationCall, code: String): RegisterPayload {
        return super.register(call, code)
    }

    @TemplateMapping("auth/register.ftl")
    @RegisterCodeRedirectPath
    override suspend fun register(call: ApplicationCall, code: String, @Payload payload: RegisterCodePayload) {
        super.register(call, code, payload)
    }

    @TemplateMapping("auth/authorize.ftl")
    @AuthorizePath
    override suspend fun authorize(call: ApplicationCall, clientId: String?): ClientForUser {
        return super.authorize(call, clientId)
    }

    @TemplateMapping("auth/authorize.ftl")
    @AuthorizeRedirectPath
    override suspend fun authorize(call: ApplicationCall, client: ClientForUser): String {
        return super.authorize(call, client)
    }

    @APIMapping
    @TokenPath
    override suspend fun token(call: ApplicationCall, @Payload request: AuthRequest): AuthToken {
        return super.token(call, request)
    }

}

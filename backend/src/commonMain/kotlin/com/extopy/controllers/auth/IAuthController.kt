package com.extopy.controllers.auth

import com.extopy.models.auth.*
import dev.kaccelero.annotations.*
import dev.kaccelero.commons.responses.RedirectResponse
import dev.kaccelero.controllers.IUnitController
import io.ktor.server.application.*

interface IAuthController : IUnitController {

    @TemplateMapping("auth/login.ftl")
    @Path("GET", "/login")
    fun login()

    @TemplateMapping("auth/login.ftl")
    @Path("POST", "/login")
    suspend fun login(
        call: ApplicationCall,
        @Payload payload: LoginPayload,
        @QueryParameter redirect: String?,
    ): RedirectResponse

    @TemplateMapping("auth/login.ftl")
    @Path("GET", "/logout")
    suspend fun logout(call: ApplicationCall, @QueryParameter redirect: String?): RedirectResponse

    @TemplateMapping("auth/register.ftl")
    @Path("GET", "/register")
    fun register()

    @TemplateMapping("auth/register.ftl")
    @Path("POST", "/register")
    suspend fun register(call: ApplicationCall, @Payload payload: RegisterPayload): Map<String, Any>

    @TemplateMapping("auth/register.ftl")
    @Path("GET", "/register/{code}")
    suspend fun registerCode(call: ApplicationCall, @PathParameter code: String): RegisterPayload

    @TemplateMapping("auth/register.ftl")
    @Path("POST", "/register/{code}")
    suspend fun registerCode(
        call: ApplicationCall,
        @PathParameter code: String,
        @Payload payload: RegisterCodePayload,
        @QueryParameter redirect: String?,
    ): RedirectResponse

    @TemplateMapping("auth/authorize.ftl")
    @Path("GET", "/authorize")
    suspend fun authorize(call: ApplicationCall, @QueryParameter clientId: String?): ClientForUser

    @TemplateMapping("auth/redirect.ftl")
    @Path("POST", "/authorize")
    suspend fun authorizeRedirect(call: ApplicationCall, @QueryParameter clientId: String?): Map<String, Any>

    @APIMapping("createToken")
    @Path("POST", "/token")
    @DocumentedTag("Auth")
    @DocumentedError(400, "auth_invalid_code")
    @DocumentedError(500, "error_internal")
    suspend fun token(@Payload payload: AuthRequest): AuthToken

    @APIMapping("refreshToken")
    @Path("POST", "/refresh")
    @DocumentedTag("Auth")
    @DocumentedError(400, "auth_invalid_credentials")
    suspend fun refreshToken(@Payload payload: RefreshTokenPayload): AuthToken

}

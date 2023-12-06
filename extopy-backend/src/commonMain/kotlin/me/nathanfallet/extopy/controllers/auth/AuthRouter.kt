package me.nathanfallet.extopy.controllers.auth

import io.ktor.server.freemarker.*
import me.nathanfallet.extopy.models.auth.LoginPayload
import me.nathanfallet.extopy.models.auth.RegisterCodePayload
import me.nathanfallet.extopy.models.auth.RegisterPayload
import me.nathanfallet.ktorx.controllers.auth.IAuthWithCodeController
import me.nathanfallet.ktorx.models.auth.AuthMapping
import me.nathanfallet.ktorx.routers.auth.LocalizedAuthWithCodeTemplateRouter
import me.nathanfallet.ktorx.usecases.localization.IGetLocaleForCallUseCase

class AuthRouter(
    controller: IAuthWithCodeController<LoginPayload, RegisterPayload, RegisterCodePayload>,
    getLocaleForCallUseCase: IGetLocaleForCallUseCase,
) : LocalizedAuthWithCodeTemplateRouter<LoginPayload, RegisterPayload, RegisterCodePayload>(
    LoginPayload::class,
    RegisterPayload::class,
    RegisterCodePayload::class,
    AuthMapping(loginTemplate = "auth/login.ftl", registerTemplate = "auth/register.ftl"),
    { template, model -> respondTemplate(template, model) },
    controller,
    getLocaleForCallUseCase
)
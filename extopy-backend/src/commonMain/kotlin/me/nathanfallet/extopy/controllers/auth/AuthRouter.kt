package me.nathanfallet.extopy.controllers.auth

import io.ktor.server.freemarker.*
import io.ktor.util.reflect.*
import me.nathanfallet.extopy.models.auth.LoginPayload
import me.nathanfallet.extopy.models.auth.RegisterCodePayload
import me.nathanfallet.extopy.models.auth.RegisterPayload
import me.nathanfallet.ktorx.controllers.auth.IAuthWithCodeController
import me.nathanfallet.ktorx.routers.api.APIUnitRouter
import me.nathanfallet.ktorx.routers.auth.LocalizedAuthWithCodeTemplateRouter
import me.nathanfallet.ktorx.routers.concat.ConcatUnitRouter
import me.nathanfallet.ktorx.usecases.localization.IGetLocaleForCallUseCase

class AuthRouter(
    controller: IAuthWithCodeController<LoginPayload, RegisterPayload, RegisterCodePayload>,
    getLocaleForCallUseCase: IGetLocaleForCallUseCase,
) : ConcatUnitRouter(
    LocalizedAuthWithCodeTemplateRouter(
        typeInfo<LoginPayload>(),
        typeInfo<RegisterPayload>(),
        typeInfo<RegisterCodePayload>(),
        controller,
        AuthController::class,
        { template, model -> respondTemplate(template, model) },
        getLocaleForCallUseCase,
        null,
        "/auth/login?redirect={path}",
        "auth/redirect.ftl",
    ),
    APIUnitRouter(
        controller,
        AuthController::class,
        route = "/auth",
        prefix = "/api/v1"
    )
)

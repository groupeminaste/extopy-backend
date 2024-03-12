package me.nathanfallet.extopy.controllers.auth

import io.ktor.server.freemarker.*
import me.nathanfallet.ktorx.routers.api.APIUnitRouter
import me.nathanfallet.ktorx.routers.concat.ConcatUnitRouter
import me.nathanfallet.ktorx.routers.templates.LocalizedTemplateUnitRouter
import me.nathanfallet.ktorx.usecases.localization.IGetLocaleForCallUseCase

class AuthRouter(
    controller: IAuthController,
    getLocaleForCallUseCase: IGetLocaleForCallUseCase,
) : ConcatUnitRouter(
    LocalizedTemplateUnitRouter(
        controller,
        IAuthController::class,
        { template, model -> respondTemplate(template, model) },
        getLocaleForCallUseCase,
        errorTemplate = null,
        redirectUnauthorizedToUrl = "/auth/login?redirect={path}",
        route = "auth",
    ),
    APIUnitRouter(
        controller,
        IAuthController::class,
        route = "auth",
        prefix = "/api/v1"
    )
)

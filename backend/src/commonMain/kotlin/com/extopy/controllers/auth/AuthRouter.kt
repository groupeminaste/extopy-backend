package com.extopy.controllers.auth

import dev.kaccelero.commons.localization.IGetLocaleForCallUseCase
import dev.kaccelero.routers.APIUnitRouter
import dev.kaccelero.routers.ConcatUnitRouter
import dev.kaccelero.routers.LocalizedTemplateUnitRouter
import io.ktor.server.freemarker.*

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

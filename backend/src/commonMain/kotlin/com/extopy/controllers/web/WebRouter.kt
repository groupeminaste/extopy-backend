package com.extopy.controllers.web

import dev.kaccelero.routers.TemplateUnitRouter
import io.ktor.server.freemarker.*

class WebRouter(
    controller: IWebController,
) : TemplateUnitRouter(
    controller,
    IWebController::class,
    { template, model ->
        respondTemplate(template, model)
    }
)

package me.nathanfallet.extopy.controllers.web

import io.ktor.server.freemarker.*
import me.nathanfallet.ktorx.routers.templates.TemplateUnitRouter

class WebRouter(
    controller: IWebController,
) : TemplateUnitRouter(
    controller,
    IWebController::class,
    { template, model ->
        respondTemplate(template, model)
    }
)

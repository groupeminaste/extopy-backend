package me.nathanfallet.extopy.controllers.web

import me.nathanfallet.ktorx.controllers.IUnitController
import me.nathanfallet.ktorx.models.annotations.Path
import me.nathanfallet.ktorx.models.annotations.TemplateMapping

interface IWebController : IUnitController {

    @TemplateMapping("index.ftl")
    @Path("GET", "/")
    fun index()

}

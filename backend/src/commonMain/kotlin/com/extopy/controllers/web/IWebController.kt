package com.extopy.controllers.web

import dev.kaccelero.annotations.Path
import dev.kaccelero.annotations.TemplateMapping
import dev.kaccelero.controllers.IUnitController

interface IWebController : IUnitController {

    @TemplateMapping("index.ftl")
    @Path("GET", "/")
    fun index()

}

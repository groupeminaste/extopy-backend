package com.extopy.plugins

import dev.kaccelero.commons.localization.TDirective
import dev.kaccelero.plugins.i18n
import freemarker.cache.ClassTemplateLoader
import io.ktor.server.application.*
import io.ktor.server.freemarker.*

fun Application.configureTemplating() {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        setSharedVariable("t", TDirective(this@configureTemplating.i18n))
    }
}

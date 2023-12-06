package me.nathanfallet.extopy.plugins

import freemarker.cache.ClassTemplateLoader
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import me.nathanfallet.ktorx.directives.TDirective
import me.nathanfallet.ktorx.plugins.i18n

fun Application.configureTemplating() {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        setSharedVariable("t", TDirective(this@configureTemplating.i18n))
    }
}

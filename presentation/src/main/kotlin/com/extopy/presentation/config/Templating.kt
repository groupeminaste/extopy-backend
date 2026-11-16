package com.extopy.presentation.config

import com.extopy.domain.services.TranslateService
import com.extopy.presentation.freemarker.TranslateDirective
import freemarker.cache.ClassTemplateLoader
import freemarker.core.HTMLOutputFormat
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import org.koin.ktor.ext.inject

fun Application.configureTemplating() {
    val translateService by inject<TranslateService>()

    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        outputFormat = HTMLOutputFormat.INSTANCE
        setSharedVariable("t", TranslateDirective(translateService))
    }
}

package com.extopy.plugins

import io.ktor.server.application.*
import dev.kaccelero.plugins.I18n
import java.util.*

fun Application.configureI18n() {
    install(I18n) {
        supportedLocales = listOf("en", "fr").map(Locale::forLanguageTag)
        useOfUri = true
    }
}

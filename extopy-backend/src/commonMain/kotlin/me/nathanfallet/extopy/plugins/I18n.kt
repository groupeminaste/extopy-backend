package me.nathanfallet.extopy.plugins

import io.ktor.server.application.*
import me.nathanfallet.ktorx.plugins.I18n
import java.util.*

fun Application.configureI18n() {
    install(I18n) {
        supportedLocales = listOf("en", "fr").map(Locale::forLanguageTag)
        useOfUri = true
    }
}

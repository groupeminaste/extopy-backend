package com.extopy.infrastructure.i18n

import com.extopy.domain.services.TranslateService
import java.text.MessageFormat
import java.util.*

/**
 * Implementation of [TranslateService] reading the `i18n/Messages_*.properties` bundles.
 */
class PropertiesTranslateService(
    private val baseName: String = DEFAULT_BUNDLE,
) : TranslateService {

    // A missing translation must not take a page down: fall back to the default language, then to
    // the key itself, which is readable enough.
    override fun translate(key: String, language: String, args: List<String>): String {
        val message = lookup(key, language) ?: lookup(key, DEFAULT_LANGUAGE) ?: return key
        if (args.isEmpty()) return message
        return MessageFormat(message, Locale.forLanguageTag(language)).format(args.toTypedArray())
    }

    private fun lookup(key: String, language: String): String? = try {
        ResourceBundle.getBundle(baseName, Locale.forLanguageTag(language)).getString(key)
    } catch (exception: MissingResourceException) {
        null
    }

    companion object {

        const val DEFAULT_BUNDLE = "i18n.Messages"
        const val DEFAULT_LANGUAGE = "en"

    }

}

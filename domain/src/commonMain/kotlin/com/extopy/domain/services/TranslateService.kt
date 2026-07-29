package com.extopy.domain.services

/**
 * Resolves the keys of the message bundle.
 */
interface TranslateService {

    /**
     * Translates a key.
     *
     * @param key The key of the message.
     * @param language The language to translate into.
     * @param args What to substitute into the placeholders of the message, in order.
     *
     * @return The translated message, falling back to the key itself if it is unknown.
     */
    fun translate(key: String, language: String, args: List<String> = emptyList()): String

}

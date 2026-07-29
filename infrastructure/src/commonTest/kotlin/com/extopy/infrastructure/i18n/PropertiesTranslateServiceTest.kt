package com.extopy.infrastructure.i18n

import kotlin.test.Test
import kotlin.test.assertEquals

class PropertiesTranslateServiceTest {

    private val service = PropertiesTranslateService()

    @Test
    fun testTranslate() {
        assertEquals("Sign in", service.translate("auth_login_title", "en"))
        assertEquals("Connexion", service.translate("auth_login_title", "fr"))
    }

    /**
     * The placeholders of a message are filled in from the arguments, in order.
     */
    @Test
    fun testTranslateWithArguments() {
        assertEquals(
            "Authorize Extopy for iOS to access to your account?",
            service.translate("auth_authorize_title", "en", listOf("Extopy for iOS"))
        )
    }

    /**
     * A missing translation must not take a page down: it falls back to English, then to the key
     * itself, which is readable enough.
     */
    @Test
    fun testAMissingTranslationFallsBack() {
        // `posts_not_found` is only in the English bundle.
        assertEquals(
            service.translate("posts_not_found", "en"),
            service.translate("posts_not_found", "fr")
        )
        assertEquals("nothing_like_this", service.translate("nothing_like_this", "en"))
    }

    @Test
    fun testAnUnknownLanguageFallsBackToEnglish() {
        assertEquals("Sign in", service.translate("auth_login_title", "de"))
    }

}

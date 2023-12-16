package me.nathanfallet.extopy.models.application

enum class ExtopyEnvironment {

    PRODUCTION, DEVELOPMENT;

    val baseUrl: String
        get() = when (this) {
            PRODUCTION -> "https://extopy.com"
            DEVELOPMENT -> "https://extopy.dev"
        }

}

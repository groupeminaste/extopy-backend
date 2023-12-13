package me.nathanfallet.extopy.models.users

import kotlinx.serialization.Serializable
import me.nathanfallet.usecases.models.annotations.Schema
import me.nathanfallet.usecases.models.annotations.validators.StringPropertyValidator

@Serializable
data class UpdateUserPayload(
    @StringPropertyValidator(regex = User.USERNAME_REGEX, maxLength = 25)
    @Schema("Username of the User", "nathanfallet")
    val username: String? = null,
    @StringPropertyValidator(maxLength = 40)
    @Schema("Display name of the User", "Nathan Fallet")
    val displayName: String? = null,
    @Schema("Password", "abc123")
    val password: String? = null,
    @Schema("Biography of the User", "Hi, I'm a developer")
    val biography: String? = null,
    @Schema("Avatar of the User", "https://...")
    val avatar: String? = null,
    @Schema("Is the User a personal (aka. private) account?", "false")
    val personal: Boolean? = null,
)

package com.extopy.models.users

import dev.kaccelero.annotations.Schema
import dev.kaccelero.annotations.StringPropertyValidator
import digital.guimauve.zodable.Zodable
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Zodable
@JsExport
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

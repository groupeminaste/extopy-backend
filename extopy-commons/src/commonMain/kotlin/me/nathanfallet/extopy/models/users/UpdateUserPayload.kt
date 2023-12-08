package me.nathanfallet.extopy.models.users

import kotlinx.serialization.Serializable
import me.nathanfallet.usecases.models.annotations.validators.StringPropertyValidator

@Serializable
data class UpdateUserPayload(
    @StringPropertyValidator(regex = User.USERNAME_REGEX, maxLength = 25)
    val username: String? = null,
    @StringPropertyValidator(maxLength = 40)
    val displayName: String? = null,
    val biography: String? = null,
    val avatar: String? = null,
    val personal: Boolean? = null,
)

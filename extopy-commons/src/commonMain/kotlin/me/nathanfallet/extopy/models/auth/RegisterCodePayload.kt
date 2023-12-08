package me.nathanfallet.extopy.models.auth

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.usecases.models.annotations.validators.StringPropertyValidator

@Serializable
data class RegisterCodePayload(
    val password: String,
    @StringPropertyValidator(regex = User.USERNAME_REGEX, maxLength = 25)
    val username: String,
    @StringPropertyValidator(maxLength = 40)
    val displayName: String,
    val birthdate: LocalDate,
)

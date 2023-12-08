package me.nathanfallet.extopy.models.users

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import me.nathanfallet.usecases.models.annotations.validators.StringPropertyValidator

@Serializable
data class CreateUserPayload(
    @StringPropertyValidator(regex = User.USERNAME_REGEX, maxLength = 25)
    val username: String,
    @StringPropertyValidator(maxLength = 40)
    val displayName: String,
    val email: String,
    val password: String,
    val birthdate: LocalDate,
)

package com.extopy.models.auth

import com.extopy.models.users.User
import dev.kaccelero.annotations.StringPropertyValidator
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class RegisterCodePayload(
    val password: String,
    @StringPropertyValidator(regex = User.USERNAME_REGEX, maxLength = 25)
    val username: String,
    @StringPropertyValidator(maxLength = 40)
    val displayName: String,
    val birthdate: LocalDate,
)

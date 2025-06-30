package com.extopy.models.users

import dev.kaccelero.annotations.StringPropertyValidator
import digital.guimauve.zodable.Zodable
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Zodable
@JsExport
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

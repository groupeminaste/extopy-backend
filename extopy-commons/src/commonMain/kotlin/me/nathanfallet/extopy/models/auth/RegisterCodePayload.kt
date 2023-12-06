package me.nathanfallet.extopy.models.auth

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class RegisterCodePayload(
    val password: String,
    val username: String,
    val displayName: String,
    val birthdate: LocalDate,
)

package com.extopy.domain.models.users

import kotlinx.datetime.LocalDate

/**
 * Everything needed to open an account.
 */
data class CreateUserPayload(
    val username: String,
    val displayName: String,
    val email: String,
    val password: String,
    val birthdate: LocalDate,
)

package com.extopy.domain.models.auth

import kotlinx.datetime.LocalDate

/**
 * What the second step of the sign-up form sends. The address is not part of it: it comes from the
 * code, so that a crafted form cannot open an account on an address it never proved it owns.
 */
data class RegisterCodePayload(
    val password: String,
    val username: String,
    val displayName: String,
    val birthdate: LocalDate,
)

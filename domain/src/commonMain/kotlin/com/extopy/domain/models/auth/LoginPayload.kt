package com.extopy.domain.models.auth

/**
 * What the sign-in form sends. The username field also accepts an email address.
 */
data class LoginPayload(
    val username: String,
    val password: String,
)

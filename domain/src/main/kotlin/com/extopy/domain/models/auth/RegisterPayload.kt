package com.extopy.domain.models.auth

/**
 * What the first step of the sign-up form sends, and what the second step reads back to fill in the
 * address the code was sent to.
 */
data class RegisterPayload(
    val email: String,
)

package com.extopy.domain.models.application

import kotlin.time.Instant

/**
 * The code sent to an email address to prove it is reachable before an account is opened.
 */
data class CodeInEmail(
    val email: String,
    val code: String,
    val expiresAt: Instant,
)

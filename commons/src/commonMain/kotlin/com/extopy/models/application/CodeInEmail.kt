package com.extopy.models.application

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class CodeInEmail(
    val email: String,
    val code: String,
    val expiresAt: Instant,
)

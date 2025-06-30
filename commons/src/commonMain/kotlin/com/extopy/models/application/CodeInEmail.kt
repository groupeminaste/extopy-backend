package com.extopy.models.application

import digital.guimauve.zodable.Zodable
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Zodable
@JsExport
@Serializable
data class CodeInEmail(
    val email: String,
    val code: String,
    val expiresAt: Instant,
)

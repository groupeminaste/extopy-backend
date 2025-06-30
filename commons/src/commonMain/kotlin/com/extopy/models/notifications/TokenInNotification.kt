package com.extopy.models.notifications

import com.extopy.models.users.User
import dev.kaccelero.models.UUID
import digital.guimauve.zodable.Zodable
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Zodable
@JsExport
@Serializable
data class TokenInNotification(
    val token: String,
    val service: String,
    val clientId: UUID,
    val userId: UUID,
    val expiresAt: Instant,
    val user: User? = null,
)

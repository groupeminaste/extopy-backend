package com.extopy.models.users

import dev.kaccelero.models.UUID
import digital.guimauve.zodable.Zodable
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Zodable
@JsExport
@Serializable
data class ClientInUser(
    val code: String,
    val userId: UUID,
    val clientId: UUID,
    val expiration: Instant,
)

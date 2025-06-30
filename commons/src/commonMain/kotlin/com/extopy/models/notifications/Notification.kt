package com.extopy.models.notifications

import dev.kaccelero.models.UUID
import digital.guimauve.zodable.Zodable
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Zodable
@JsExport
@Serializable
data class Notification(
    val id: UUID,
    val userId: UUID?,
    val type: String?,
    val body: String?,
    val contentId: UUID?,
    val publishedAt: Instant? = null,
    val expiresAt: Instant? = null,
    val read: Boolean? = null,
)

package com.extopy.models.auth

import dev.kaccelero.commons.auth.ISessionPayload
import dev.kaccelero.models.UUID
import digital.guimauve.zodable.Zodable
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Zodable
@JsExport
@Serializable
data class SessionPayload(
    val userId: UUID,
) : ISessionPayload

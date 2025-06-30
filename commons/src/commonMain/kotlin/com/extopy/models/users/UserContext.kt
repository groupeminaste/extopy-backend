package com.extopy.models.users

import dev.kaccelero.models.IContext
import dev.kaccelero.models.UUID
import digital.guimauve.zodable.Zodable
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Zodable
@JsExport
@Serializable
data class UserContext(
    val userId: UUID,
) : IContext

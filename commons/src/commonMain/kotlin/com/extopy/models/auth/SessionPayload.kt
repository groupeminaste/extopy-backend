package com.extopy.models.auth

import dev.kaccelero.commons.auth.ISessionPayload
import dev.kaccelero.models.UUID
import kotlinx.serialization.Serializable

@Serializable
data class SessionPayload(
    val userId: UUID,
) : ISessionPayload

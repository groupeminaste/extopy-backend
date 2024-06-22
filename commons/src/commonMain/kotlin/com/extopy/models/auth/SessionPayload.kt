package com.extopy.models.auth

import dev.kaccelero.commons.auth.ISessionPayload
import kotlinx.serialization.Serializable

@Serializable
data class SessionPayload(
    val userId: String,
) : ISessionPayload

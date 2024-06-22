package com.extopy.models.users

import dev.kaccelero.models.UUID
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ClientInUser(
    val code: String,
    val userId: UUID,
    val clientId: UUID,
    val expiration: Instant,
)

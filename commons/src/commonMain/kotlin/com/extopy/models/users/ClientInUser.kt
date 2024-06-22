package com.extopy.models.users

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ClientInUser(
    val code: String,
    val userId: String,
    val clientId: String,
    val expiration: Instant,
)

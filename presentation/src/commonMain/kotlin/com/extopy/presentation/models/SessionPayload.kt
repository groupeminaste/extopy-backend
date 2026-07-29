package com.extopy.presentation.models

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

/**
 * What the session cookie of the web pages holds.
 */
@Serializable
data class SessionPayload(
    val userId: Uuid,
)

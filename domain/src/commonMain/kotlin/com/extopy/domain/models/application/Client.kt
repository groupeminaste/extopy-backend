package com.extopy.domain.models.application

import kotlin.uuid.Uuid

/**
 * An OAuth client allowed to ask a user for access to its account.
 */
data class Client(
    val id: Uuid,
    val ownerId: Uuid,
    val name: String,
    val description: String,
    val secret: String,
    val redirectUri: String,
)

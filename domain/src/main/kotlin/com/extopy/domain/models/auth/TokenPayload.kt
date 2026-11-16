package com.extopy.domain.models.auth

import kotlin.uuid.Uuid

/**
 * What a verified token says: which user it stands for, and which client it was issued to.
 */
data class TokenPayload(
    val userId: Uuid,
    val clientId: Uuid?,
)

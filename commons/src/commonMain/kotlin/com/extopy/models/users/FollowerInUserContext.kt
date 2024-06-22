package com.extopy.models.users

import dev.kaccelero.models.IContext
import dev.kaccelero.models.UUID

data class FollowerInUserContext(
    val userId: UUID,
    val isTargetPublic: Boolean,
) : IContext

package com.extopy.models.users

import dev.kaccelero.models.IContext
import dev.kaccelero.models.UUID

data class UserContext(
    val userId: UUID,
) : IContext

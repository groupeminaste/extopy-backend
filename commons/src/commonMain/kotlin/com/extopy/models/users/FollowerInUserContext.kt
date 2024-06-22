package com.extopy.models.users

import dev.kaccelero.models.IContext

data class FollowerInUserContext(
    val userId: String,
    val isTargetPublic: Boolean,
) : IContext

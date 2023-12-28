package me.nathanfallet.extopy.models.users

import me.nathanfallet.usecases.context.IContext

data class FollowerInUserContext(
    val userId: String,
    val isTargetPublic: Boolean,
) : IContext

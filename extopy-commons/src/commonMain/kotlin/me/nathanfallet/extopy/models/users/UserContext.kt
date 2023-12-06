package me.nathanfallet.extopy.models.users

import me.nathanfallet.usecases.context.IContext

data class UserContext(
    val user: User,
) : IContext

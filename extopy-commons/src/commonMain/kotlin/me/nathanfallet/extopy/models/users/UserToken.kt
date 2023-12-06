package me.nathanfallet.extopy.models.users

import kotlinx.serialization.Serializable

@Serializable
data class UserToken(val token: String, val user: User)

package me.nathanfallet.extopy.models.auth

import kotlinx.serialization.Serializable
import me.nathanfallet.usecases.users.ISessionPayload

@Serializable
data class SessionPayload(
    val userId: String,
) : ISessionPayload

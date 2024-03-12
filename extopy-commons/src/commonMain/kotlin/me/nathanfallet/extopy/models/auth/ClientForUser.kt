package me.nathanfallet.extopy.models.auth

import kotlinx.serialization.Serializable
import me.nathanfallet.extopy.models.application.Client
import me.nathanfallet.extopy.models.users.User

@Serializable
data class ClientForUser(
    val client: Client,
    val user: User,
)

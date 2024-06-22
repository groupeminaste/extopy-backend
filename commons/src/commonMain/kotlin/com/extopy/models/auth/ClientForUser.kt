package com.extopy.models.auth

import kotlinx.serialization.Serializable
import com.extopy.models.application.Client
import com.extopy.models.users.User

@Serializable
data class ClientForUser(
    val client: Client,
    val user: User,
)

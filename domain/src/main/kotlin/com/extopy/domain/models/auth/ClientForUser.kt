package com.extopy.domain.models.auth

import com.extopy.domain.models.application.Client
import com.extopy.domain.models.users.User

/**
 * A client, together with the user that authorized it.
 */
data class ClientForUser(
    val client: Client,
    val user: User,
)

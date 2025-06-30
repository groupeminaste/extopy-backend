package com.extopy.models.auth

import com.extopy.models.application.Client
import com.extopy.models.users.User
import digital.guimauve.zodable.Zodable
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Zodable
@JsExport
@Serializable
data class ClientForUser(
    val client: Client,
    val user: User,
)

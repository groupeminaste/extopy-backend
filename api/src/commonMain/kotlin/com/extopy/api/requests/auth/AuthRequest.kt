package com.extopy.api.requests.auth

import dev.zodable.Zodable
import kotlinx.serialization.Serializable
import kotlin.js.JsExport
import kotlin.uuid.Uuid

/**
 * The body a client sends to exchange an authorization code for a token.
 */
@Zodable
@JsExport
@Serializable
data class AuthRequest(
    val clientId: Uuid,
    val clientSecret: String,
    val code: String,
    val codeVerifier: String? = null,
)

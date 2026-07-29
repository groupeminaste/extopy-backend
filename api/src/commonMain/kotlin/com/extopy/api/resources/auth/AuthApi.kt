package com.extopy.api.resources.auth

import io.ktor.resources.*
import kotlinx.serialization.Serializable

/**
 * Resource class representing the authentication endpoints of the API.
 */
@Serializable
@Resource("/api/v1/auth")
class AuthApi {

    /**
     * Resource class representing the endpoint exchanging an authorization code for a token.
     */
    @Resource("token")
    class Token(
        /**
         * The parent AuthApi resource.
         */
        val parent: AuthApi = AuthApi(),
    )

    /**
     * Resource class representing the endpoint exchanging a refresh token for a new token.
     */
    @Resource("refresh")
    class Refresh(
        /**
         * The parent AuthApi resource.
         */
        val parent: AuthApi = AuthApi(),
    )

}

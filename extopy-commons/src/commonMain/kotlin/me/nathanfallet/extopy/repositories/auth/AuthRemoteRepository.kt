package me.nathanfallet.extopy.repositories.auth

import me.nathanfallet.extopy.client.IExtopyClient
import me.nathanfallet.ktorx.repositories.auth.AuthAPIRemoteRepository

class AuthRemoteRepository(
    client: IExtopyClient,
) : AuthAPIRemoteRepository(
    client,
    prefix = "/api/v1"
), IAuthRemoteRepository {
}

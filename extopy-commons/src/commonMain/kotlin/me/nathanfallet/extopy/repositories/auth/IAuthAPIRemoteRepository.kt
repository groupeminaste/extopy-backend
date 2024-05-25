package me.nathanfallet.extopy.repositories.auth

import me.nathanfallet.extopy.models.auth.RefreshTokenPayload
import me.nathanfallet.usecases.auth.AuthRequest
import me.nathanfallet.usecases.auth.AuthToken

interface IAuthAPIRemoteRepository {

    suspend fun token(payload: AuthRequest): AuthToken?
    suspend fun refresh(payload: RefreshTokenPayload): AuthToken?

}

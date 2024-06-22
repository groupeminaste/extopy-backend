package com.extopy.repositories.auth

import com.extopy.models.auth.AuthRequest
import com.extopy.models.auth.AuthToken
import com.extopy.models.auth.RefreshTokenPayload

interface IAuthAPIRemoteRepository {

    suspend fun token(payload: AuthRequest): AuthToken?
    suspend fun refresh(payload: RefreshTokenPayload): AuthToken?

}

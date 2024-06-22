package com.extopy.repositories.auth

import com.extopy.client.IExtopyClient
import com.extopy.models.auth.AuthRequest
import com.extopy.models.auth.AuthToken
import com.extopy.models.auth.RefreshTokenPayload
import dev.kaccelero.models.RecursiveId
import dev.kaccelero.models.UnitModel
import dev.kaccelero.repositories.APIUnitRemoteRepository
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

open class AuthAPIRemoteRepository(
    client: IExtopyClient,
) : APIUnitRemoteRepository(
    client,
    route = "auth",
    prefix = "/api/v1"
), IAuthAPIRemoteRepository {

    override suspend fun token(
        payload: AuthRequest,
    ): AuthToken? = client.request(
        HttpMethod.Post,
        "${constructFullRoute(RecursiveId<UnitModel, Unit, Unit>(Unit))}/token"
    ) {
        contentType(ContentType.Application.Json)
        setBody(payload)
    }.body()

    override suspend fun refresh(payload: RefreshTokenPayload): AuthToken? = client.request(
        HttpMethod.Post,
        "${constructFullRoute(RecursiveId<UnitModel, Unit, Unit>(Unit))}/refresh"
    ) {
        contentType(ContentType.Application.Json)
        setBody(payload)
    }.body()

}

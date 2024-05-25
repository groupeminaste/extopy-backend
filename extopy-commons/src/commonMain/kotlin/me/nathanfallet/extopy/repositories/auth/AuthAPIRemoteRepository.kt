package me.nathanfallet.extopy.repositories.auth

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import me.nathanfallet.extopy.models.auth.RefreshTokenPayload
import me.nathanfallet.ktorx.models.api.IAPIClient
import me.nathanfallet.ktorx.repositories.api.APIUnitRemoteRepository
import me.nathanfallet.usecases.auth.AuthRequest
import me.nathanfallet.usecases.auth.AuthToken
import me.nathanfallet.usecases.models.UnitModel
import me.nathanfallet.usecases.models.id.RecursiveId

open class AuthAPIRemoteRepository(
    client: IAPIClient,
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

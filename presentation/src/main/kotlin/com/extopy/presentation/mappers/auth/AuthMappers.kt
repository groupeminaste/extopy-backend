package com.extopy.presentation.mappers.auth

import com.extopy.api.responses.auth.AuthTokenResponse
import com.extopy.domain.models.application.Client
import com.extopy.domain.models.auth.AuthToken
import com.extopy.domain.models.auth.ClientForUser
import com.extopy.presentation.mappers.users.toUserView
import com.extopy.presentation.views.ClientForUserView
import com.extopy.presentation.views.ClientView

/**
 * Maps an [AuthToken] to an [AuthTokenResponse].
 */
fun AuthToken.toAuthTokenResponse() = AuthTokenResponse(
    accessToken = accessToken,
    refreshToken = refreshToken,
    idToken = idToken,
)

/**
 * Maps a [Client] to a [ClientView].
 *
 * The secret stays behind: the page only names the client and says what it is for.
 */
fun Client.toClientView() = ClientView(
    id = id.toString(),
    name = name,
    description = description,
)

/**
 * Maps a [ClientForUser] to the view the `authorize` page reads.
 */
fun ClientForUser.toClientForUserView() = ClientForUserView(
    client = client.toClientView(),
    user = user.toUserView(),
)

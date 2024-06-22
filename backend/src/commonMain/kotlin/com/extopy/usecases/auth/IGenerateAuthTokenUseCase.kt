package com.extopy.usecases.auth

import com.extopy.models.auth.AuthToken
import com.extopy.models.auth.ClientForUser
import dev.kaccelero.usecases.ISuspendUseCase

interface IGenerateAuthTokenUseCase : ISuspendUseCase<ClientForUser, AuthToken>

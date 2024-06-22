package com.extopy.usecases.auth

import com.extopy.models.auth.ClientForUser
import dev.kaccelero.usecases.ISuspendUseCase

interface IGetClientForUserForRefreshTokenUseCase : ISuspendUseCase<String, ClientForUser?>

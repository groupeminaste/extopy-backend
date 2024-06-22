package com.extopy.usecases.auth

import com.extopy.models.auth.SessionPayload
import dev.kaccelero.usecases.IUseCase
import io.ktor.server.application.*

interface IGetSessionForCallUseCase : IUseCase<ApplicationCall, SessionPayload?>

package com.extopy.usecases.auth

import com.extopy.models.auth.SessionPayload
import dev.kaccelero.usecases.IPairUseCase
import io.ktor.server.application.*

interface ISetSessionForCallUseCase : IPairUseCase<ApplicationCall, SessionPayload, Unit>

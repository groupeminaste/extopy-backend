package me.nathanfallet.extopy.usecases.auth

import io.ktor.server.application.*
import me.nathanfallet.extopy.models.auth.SessionPayload
import me.nathanfallet.usecases.base.IPairUseCase

interface ISetSessionForCallUseCase : IPairUseCase<ApplicationCall, SessionPayload, Unit>

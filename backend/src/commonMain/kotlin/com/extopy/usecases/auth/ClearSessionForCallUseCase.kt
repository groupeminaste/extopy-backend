package com.extopy.usecases.auth

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import com.extopy.models.auth.SessionPayload

class ClearSessionForCallUseCase : IClearSessionForCallUseCase {

    override fun invoke(input: ApplicationCall) = input.sessions.clear<SessionPayload>()

}

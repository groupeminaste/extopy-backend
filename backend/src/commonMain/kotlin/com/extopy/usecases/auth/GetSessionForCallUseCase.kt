package com.extopy.usecases.auth

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import com.extopy.models.auth.SessionPayload

class GetSessionForCallUseCase : IGetSessionForCallUseCase {

    override fun invoke(input: ApplicationCall): SessionPayload? = input.sessions.get()

}

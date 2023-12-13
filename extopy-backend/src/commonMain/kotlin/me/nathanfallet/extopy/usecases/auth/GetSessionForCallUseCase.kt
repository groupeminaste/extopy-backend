package me.nathanfallet.extopy.usecases.auth

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import me.nathanfallet.extopy.models.auth.SessionPayload
import me.nathanfallet.ktorx.usecases.auth.IGetSessionForCallUseCase
import me.nathanfallet.usecases.auth.ISessionPayload

class GetSessionForCallUseCase : IGetSessionForCallUseCase {

    override fun invoke(input: ApplicationCall): ISessionPayload? {
        return input.sessions.get<SessionPayload>()
    }

}

package me.nathanfallet.extopy.usecases.auth

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import me.nathanfallet.extopy.models.auth.SessionPayload
import me.nathanfallet.ktorx.usecases.auth.ISetSessionForCallUseCase
import me.nathanfallet.usecases.auth.ISessionPayload

class SetSessionForCallUseCase : ISetSessionForCallUseCase {

    override fun invoke(input1: ApplicationCall, input2: ISessionPayload) {
        if (input2 !is SessionPayload) return
        input1.sessions.set(input2)
    }

}

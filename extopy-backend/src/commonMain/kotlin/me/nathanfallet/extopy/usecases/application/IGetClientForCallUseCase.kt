package me.nathanfallet.extopy.usecases.application

import io.ktor.server.application.*
import me.nathanfallet.usecases.auth.IClient
import me.nathanfallet.usecases.base.ISuspendUseCase

interface IGetClientForCallUseCase : ISuspendUseCase<ApplicationCall, IClient?>

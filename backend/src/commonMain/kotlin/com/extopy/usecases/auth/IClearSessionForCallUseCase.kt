package com.extopy.usecases.auth

import dev.kaccelero.usecases.IUseCase
import io.ktor.server.application.*

interface IClearSessionForCallUseCase : IUseCase<ApplicationCall, Unit>

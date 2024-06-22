package com.extopy.usecases.auth

import com.extopy.models.auth.RegisterCodePayload
import com.extopy.models.users.User
import dev.kaccelero.usecases.IPairSuspendUseCase

interface IRegisterUseCase : IPairSuspendUseCase<String, RegisterCodePayload, User?>

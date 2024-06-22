package com.extopy.usecases.auth

import com.extopy.models.auth.LoginPayload
import com.extopy.models.users.User
import dev.kaccelero.usecases.ISuspendUseCase

interface ILoginUseCase : ISuspendUseCase<LoginPayload, User?>

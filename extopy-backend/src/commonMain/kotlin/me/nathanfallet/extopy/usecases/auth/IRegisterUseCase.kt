package me.nathanfallet.extopy.usecases.auth

import me.nathanfallet.extopy.models.auth.RegisterCodePayload
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.usecases.base.IPairSuspendUseCase

interface IRegisterUseCase : IPairSuspendUseCase<String, RegisterCodePayload, User?>

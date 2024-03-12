package me.nathanfallet.extopy.usecases.auth

import me.nathanfallet.extopy.models.auth.LoginPayload
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.usecases.base.ISuspendUseCase

interface ILoginUseCase : ISuspendUseCase<LoginPayload, User?>

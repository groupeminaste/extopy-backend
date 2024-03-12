package me.nathanfallet.extopy.usecases.auth

import me.nathanfallet.extopy.models.auth.ClientForUser
import me.nathanfallet.usecases.base.ISuspendUseCase

interface ICreateAuthCodeUseCase : ISuspendUseCase<ClientForUser, String?>

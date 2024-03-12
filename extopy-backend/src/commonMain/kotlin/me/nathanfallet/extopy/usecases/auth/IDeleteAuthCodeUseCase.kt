package me.nathanfallet.extopy.usecases.auth

import me.nathanfallet.usecases.base.ISuspendUseCase

interface IDeleteAuthCodeUseCase : ISuspendUseCase<String, Boolean>

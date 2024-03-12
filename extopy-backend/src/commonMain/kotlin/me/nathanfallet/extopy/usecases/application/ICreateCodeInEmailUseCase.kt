package me.nathanfallet.extopy.usecases.application

import me.nathanfallet.extopy.models.application.CodeInEmail
import me.nathanfallet.usecases.base.ISuspendUseCase

interface ICreateCodeInEmailUseCase : ISuspendUseCase<String, CodeInEmail?>

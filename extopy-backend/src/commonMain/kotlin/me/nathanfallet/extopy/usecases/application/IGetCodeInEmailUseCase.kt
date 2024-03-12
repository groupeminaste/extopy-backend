package me.nathanfallet.extopy.usecases.application

import me.nathanfallet.extopy.models.application.CodeInEmail
import me.nathanfallet.usecases.base.ISuspendUseCase

interface IGetCodeInEmailUseCase : ISuspendUseCase<String, CodeInEmail?>

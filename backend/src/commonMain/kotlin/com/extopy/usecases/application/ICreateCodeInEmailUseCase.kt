package com.extopy.usecases.application

import com.extopy.models.application.CodeInEmail
import dev.kaccelero.usecases.ISuspendUseCase

interface ICreateCodeInEmailUseCase : ISuspendUseCase<String, CodeInEmail?>

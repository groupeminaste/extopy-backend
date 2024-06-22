package com.extopy.usecases.auth

import com.extopy.models.auth.ClientForUser
import dev.kaccelero.usecases.ISuspendUseCase

interface IGetAuthCodeUseCase : ISuspendUseCase<String, ClientForUser?>

package com.extopy.usecases.application

import dev.kaccelero.usecases.ISuspendUseCase
import kotlinx.datetime.Instant

interface IExpireUseCase : ISuspendUseCase<Instant, Unit>

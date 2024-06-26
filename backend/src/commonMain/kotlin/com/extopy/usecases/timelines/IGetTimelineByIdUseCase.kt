package com.extopy.usecases.timelines

import com.extopy.models.timelines.Timeline
import com.extopy.models.users.UserContext
import dev.kaccelero.models.UUID
import dev.kaccelero.usecases.IPairSuspendUseCase

interface IGetTimelineByIdUseCase : IPairSuspendUseCase<UUID, UserContext, Timeline?>

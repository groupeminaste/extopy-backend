package me.nathanfallet.extopy.usecases.timelines

import me.nathanfallet.extopy.models.timelines.Timeline
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.usecases.base.ISuspendUseCase

interface IGetDefaultTimelineUseCase : ISuspendUseCase<UserContext, Timeline>

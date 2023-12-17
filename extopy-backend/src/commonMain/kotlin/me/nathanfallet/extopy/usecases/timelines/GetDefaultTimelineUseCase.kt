package me.nathanfallet.extopy.usecases.timelines

import me.nathanfallet.extopy.models.timelines.Timeline
import me.nathanfallet.extopy.models.users.UserContext

class GetDefaultTimelineUseCase : IGetDefaultTimelineUseCase {

    override suspend fun invoke(input: UserContext): Timeline {
        TODO("Not yet implemented")
    }

}

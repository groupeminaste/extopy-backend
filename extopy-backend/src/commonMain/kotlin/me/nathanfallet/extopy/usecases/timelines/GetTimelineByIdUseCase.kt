package me.nathanfallet.extopy.usecases.timelines

import me.nathanfallet.extopy.models.timelines.Timeline
import me.nathanfallet.extopy.models.users.UserContext

class GetTimelineByIdUseCase : IGetTimelineByIdUseCase {

    override suspend fun invoke(input1: String, input2: UserContext): Timeline? {
        return when (input1) {
            "default" -> Timeline("default")
            "trends" -> Timeline("trends")
            else -> null // TODO: Custom timelines
        }
    }

}

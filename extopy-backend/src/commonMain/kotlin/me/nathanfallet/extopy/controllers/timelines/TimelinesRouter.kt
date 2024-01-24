package me.nathanfallet.extopy.controllers.timelines

import io.ktor.util.reflect.*
import me.nathanfallet.extopy.models.timelines.Timeline
import me.nathanfallet.ktorx.routers.api.APIModelRouter

class TimelinesRouter(
    controller: ITimelinesController,
) : APIModelRouter<Timeline, String, Unit, Unit>(
    typeInfo<Timeline>(),
    typeInfo<Unit>(),
    typeInfo<Unit>(),
    controller,
    ITimelinesController::class,
    prefix = "/api/v1"
)

package com.extopy.controllers.timelines

import com.extopy.models.timelines.Timeline
import dev.kaccelero.routers.APIModelRouter
import io.ktor.util.reflect.*

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

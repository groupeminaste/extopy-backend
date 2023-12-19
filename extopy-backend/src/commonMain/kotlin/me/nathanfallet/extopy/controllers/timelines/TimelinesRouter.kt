package me.nathanfallet.extopy.controllers.timelines

import io.ktor.util.reflect.*
import me.nathanfallet.extopy.models.timelines.Timeline
import me.nathanfallet.ktorx.controllers.IModelController
import me.nathanfallet.ktorx.models.api.APIMapping
import me.nathanfallet.ktorx.routers.api.APIModelRouter

class TimelinesRouter(
    timelinesController: IModelController<Timeline, String, Unit, Unit>,
) : APIModelRouter<Timeline, String, Unit, Unit>(
    typeInfo<Timeline>(),
    typeInfo<Unit>(),
    typeInfo<Unit>(),
    typeInfo<List<Timeline>>(),
    timelinesController,
    mapping = APIMapping(
        listEnabled = false,
        createEnabled = false,
        updateEnabled = false,
        deleteEnabled = false
    ),
    prefix = "/api/v1"
)

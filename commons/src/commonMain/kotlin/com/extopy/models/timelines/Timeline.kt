package com.extopy.models.timelines

import dev.kaccelero.annotations.Schema
import dev.kaccelero.models.IModel
import dev.kaccelero.models.UUID
import kotlinx.serialization.Serializable

// Note: For now we only support default timeline, but we will add more later (custom timelines)
@Serializable
data class Timeline(
    @Schema("Id of the Timeline", "123abc")
    override val id: UUID,
) : IModel<UUID, Unit, Unit> {

    companion object {

        val defaultId = UUID("00000000-0000-0000-0000-000000000000")

    }

}

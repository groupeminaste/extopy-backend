package me.nathanfallet.extopy.models.timelines

import kotlinx.serialization.Serializable
import me.nathanfallet.usecases.models.IModel
import me.nathanfallet.usecases.models.annotations.Schema

// Note: For now we only support default timeline, but we will add more later (custom timelines)
@Serializable
data class Timeline(
    @Schema("Id of the Timeline", "123abc")
    override val id: String,
) : IModel<String, Unit, Unit>

package com.extopy.models.users

import dev.kaccelero.models.IChildModel
import dev.kaccelero.models.UUID
import digital.guimauve.zodable.Zodable
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Zodable
@JsExport
@Serializable
data class FollowerInUser(
    val userId: UUID,
    val targetId: UUID,
    val accepted: Boolean?,
    val user: User?,
    val target: User?,
) : IChildModel<UUID, Unit, Unit, UUID> {

    override val id: UUID
        get() = userId

    override val parentId: UUID
        get() = targetId

}

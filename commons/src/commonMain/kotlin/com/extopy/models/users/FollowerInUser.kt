package com.extopy.models.users

import dev.kaccelero.models.IChildModel
import dev.kaccelero.models.UUID
import kotlinx.serialization.Serializable

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

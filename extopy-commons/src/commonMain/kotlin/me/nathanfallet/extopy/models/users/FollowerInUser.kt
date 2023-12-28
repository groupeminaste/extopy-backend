package me.nathanfallet.extopy.models.users

import kotlinx.serialization.Serializable
import me.nathanfallet.usecases.models.IChildModel

@Serializable
data class FollowerInUser(
    val userId: String,
    val targetId: String,
    val accepted: Boolean?,
    val user: User?,
    val target: User?,
) : IChildModel<String, Unit, Unit, String> {

    override val id = userId
    override val parentId = targetId

}

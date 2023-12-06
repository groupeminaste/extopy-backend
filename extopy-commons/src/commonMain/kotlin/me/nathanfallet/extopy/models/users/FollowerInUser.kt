package me.nathanfallet.extopy.models.users

import kotlinx.serialization.Serializable

@Serializable
data class FollowerInUser(
    val userId: String,
    val targetId: String,
    val accepted: Boolean?,
    val user: User?,
    val target: User?,
)

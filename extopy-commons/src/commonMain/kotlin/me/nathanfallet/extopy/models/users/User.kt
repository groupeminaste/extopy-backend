package me.nathanfallet.extopy.models.users

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import me.nathanfallet.usecases.models.IModel
import me.nathanfallet.usecases.users.IUser

@Serializable
data class User(
    override val id: String,
    val displayName: String,
    val username: String,
    val email: String?,
    val password: String?,
    val biography: String?,
    val avatar: String?,
    val birthdate: LocalDate?,
    val joinDate: Instant?,
    val lastActive: Instant?,
    val personal: Boolean?,
    val verified: Boolean?,
    val banned: Boolean?,
    val postsCount: Long?,
    val followersCount: Long?,
    val followingCount: Long?,
    val followersIn: Boolean?,
    val followingIn: Boolean?,
) : IModel<String, CreateUserPayload, UpdateUserPayload>, IUser

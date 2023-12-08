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
    val email: String? = null,
    val password: String? = null,
    val biography: String? = null,
    val avatar: String? = null,
    val birthdate: LocalDate? = null,
    val joinDate: Instant? = null,
    val lastActive: Instant? = null,
    val personal: Boolean? = null,
    val verified: Boolean? = null,
    val banned: Boolean? = null,
    val postsCount: Long? = null,
    val followersCount: Long? = null,
    val followingCount: Long? = null,
    val followersIn: Boolean? = null,
    val followingIn: Boolean? = null,
) : IModel<String, CreateUserPayload, UpdateUserPayload>, IUser {

    companion object {

        const val USERNAME_REGEX = "[a-zA-Z0-9_]+"

    }

}

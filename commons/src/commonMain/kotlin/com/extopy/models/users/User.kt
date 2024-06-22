package com.extopy.models.users

import dev.kaccelero.annotations.Schema
import dev.kaccelero.models.IModel
import dev.kaccelero.models.IUser
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @Schema("Id of the User", "123abc")
    override val id: String,
    @Schema("Display name of the User", "Nathan Fallet")
    val displayName: String,
    @Schema("Username of the User", "nathanfallet")
    val username: String,
    @Schema("Email of the User", "nathan@extopy.com")
    val email: String? = null,
    val password: String? = null,
    @Schema("Biography of the User", "Hi, I'm a developer")
    val biography: String? = null,
    @Schema("Avatar of the User", "https://...")
    val avatar: String? = null,
    @Schema("Birthdate of the User", "2002-12-24")
    val birthdate: LocalDate? = null,
    @Schema("Join date of the User", "2023-12-13T09:41:00Z")
    val joinDate: Instant? = null,
    @Schema("Last active date of the User", "2023-12-13T09:41:00Z")
    val lastActive: Instant? = null,
    @Schema("Is the User a personal (aka. private) account?", "false")
    val personal: Boolean? = null,
    @Schema("Is the User verified?", "true")
    val verified: Boolean? = null,
    @Schema("Is the User banned?", "false")
    val banned: Boolean? = null,
    @Schema("Number of posts of the User", "123")
    val postsCount: Long? = null,
    @Schema("Number of followers of the User", "123")
    val followersCount: Long? = null,
    @Schema("Number of following of the User", "123")
    val followingCount: Long? = null,
    @Schema("Is the current user following this user?", "true")
    val followersIn: Boolean? = null,
    @Schema("Is the current user followed by this user?", "true")
    val followingIn: Boolean? = null,
) : IModel<String, CreateUserPayload, UpdateUserPayload>, IUser {

    companion object {

        const val USERNAME_REGEX = "[a-zA-Z0-9_]+"

    }

}

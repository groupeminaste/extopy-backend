package com.extopy.database.users

import com.extopy.database.posts.Posts
import com.extopy.models.users.User
import dev.kaccelero.models.UUID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.countDistinct
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object Users : UUIDTable() {

    val displayName = varchar("displayname", 255)
    val username = varchar("username", 255)
    val email = varchar("email", 255)
    val password = varchar("password", 255)
    val biography = text("biography")
    val avatar = varchar("avatar", 255).default("https://extopy.com/img/avatar.png")
    val birthdate = date("birthdate")
    val joinDate = timestamp("join_date")
    val lastActive = timestamp("last_active")
    val personal = bool("personal").default(false)
    val verified = bool("verified").default(false)
    val banned = bool("banned").default(false)

    val postsCount = Posts.id.countDistinct()
    val followersCount = FollowersInUsers.userId.countDistinct()
    val followingCount = FollowersInUsers.following[FollowersInUsers.targetId].countDistinct()
    val followersIn = FollowersInUsers.followersIn[FollowersInUsers.userId].countDistinct()
    val followingIn = FollowersInUsers.followingIn[FollowersInUsers.targetId].countDistinct()

    fun toUser(
        row: ResultRow,
        includePassword: Boolean = false,
    ) = User(
        UUID(row[id].value),
        row[displayName],
        row[username],
        row.getOrNull(email),
        row.getOrNull(password).takeIf { includePassword },
        row.getOrNull(biography),
        row.getOrNull(avatar),
        row.getOrNull(birthdate),
        row.getOrNull(joinDate),
        row.getOrNull(lastActive),
        row.getOrNull(personal),
        row.getOrNull(verified),
        row.getOrNull(banned),
        row.getOrNull(postsCount),
        row.getOrNull(followersCount),
        row.getOrNull(followingCount),
        row.getOrNull(followersIn)?.let { it >= 1L },
        row.getOrNull(followingIn)?.let { it >= 1L }
    )

}

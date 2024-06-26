package com.extopy.database.users

import com.extopy.models.users.FollowerInUser
import com.extopy.models.users.User
import dev.kaccelero.models.UUID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.alias

object FollowersInUsers : Table() {

    val userId = uuid("user_id").index()
    val targetId = uuid("target_id").index()
    val accepted = bool("accepted")

    val following = FollowersInUsers.alias("UsersFollowing")
    val followersIn = FollowersInUsers.alias("UsersFollowersIn")
    val followingIn = FollowersInUsers.alias("UsersFollowingIn")

    override val primaryKey = PrimaryKey(arrayOf(userId, targetId))

    fun toFollowerInUser(
        row: ResultRow,
        user: User?,
        target: User?,
    ) = FollowerInUser(
        UUID(row[userId]),
        UUID(row[targetId]),
        row.getOrNull(accepted),
        user,
        target
    )

}

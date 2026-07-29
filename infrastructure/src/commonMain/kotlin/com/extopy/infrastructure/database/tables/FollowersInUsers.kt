package com.extopy.infrastructure.database.tables

import com.extopy.domain.models.users.FollowerInUser
import com.extopy.domain.models.users.User
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.alias

object FollowersInUsers : Table() {

    val userId = uuid("user_id").index()
    val targetId = uuid("target_id").index()
    val accepted = bool("accepted")

    /**
     * The same table joined again, once per counter and per flag a user carries: how many accounts
     * it follows, and whether the account doing the reading follows it or is followed by it.
     */
    val following = FollowersInUsers.alias("UsersFollowing")
    val followersIn = FollowersInUsers.alias("UsersFollowersIn")
    val followingIn = FollowersInUsers.alias("UsersFollowingIn")

    override val primaryKey = PrimaryKey(arrayOf(userId, targetId))

    fun toFollowerInUser(
        row: ResultRow,
        user: User? = null,
        target: User? = null,
    ) = FollowerInUser(
        row[userId],
        row[targetId],
        row.getOrNull(accepted),
        user,
        target
    )

}

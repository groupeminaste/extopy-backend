package me.nathanfallet.extopy.database.users

import me.nathanfallet.extopy.models.users.FollowerInUser
import me.nathanfallet.extopy.models.users.User
import org.jetbrains.exposed.sql.*

object FollowersInUsers : Table() {

    val userId = varchar("user_id", 32)
    val targetId = varchar("target_id", 32)
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
        row[userId],
        row[targetId],
        row.getOrNull(accepted),
        user,
        target
    )

    fun customFollowingJoin(): FieldSet {
        return FollowersInUsers
            .join(Users, JoinType.LEFT, targetId, Users.id)
            .customUsersFollowersSlice()
    }

    fun customFollowersJoin(): FieldSet {
        return FollowersInUsers
            .join(Users, JoinType.LEFT, userId, Users.id)
            .customUsersFollowersSlice()
    }

}

fun ColumnSet.customUsersFollowersSlice(additionalFields: List<Expression<*>> = listOf()): FieldSet {
    return slice(
        additionalFields +
                FollowersInUsers.userId +
                FollowersInUsers.targetId +
                Users.id +
                Users.displayName +
                Users.username +
                Users.avatar +
                Users.verified
    )
}

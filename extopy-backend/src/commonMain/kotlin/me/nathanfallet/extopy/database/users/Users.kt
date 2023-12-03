package me.nathanfallet.extopy.database.users

import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDate
import kotlinx.serialization.Serializable
import me.nathanfallet.extopy.database.posts.Posts
import me.nathanfallet.extopy.extensions.generateId
import me.nathanfallet.extopy.models.users.User
import org.jetbrains.exposed.sql.*

object Users : Table() {

    val id = varchar("id", 32)
    val displayName = varchar("displayname", 255)
    val username = varchar("username", 255)
    val email = varchar("email", 255)
    val password = varchar("password", 255)
    val biography = text("biography")
    val avatar = varchar("avatar", 255).default("https://extopy.com/img/avatar.png")
    val birthdate = varchar("birthdate", 255)
    val joinDate = varchar("join_date", 255)
    val lastActive = varchar("last_active", 255)
    val personal = bool("personal").default(false)
    val emailVerified = bool("email_verified").default(false)
    val verified = bool("verified").default(false)
    val banned = bool("banned").default(false)

    val postsCount = Posts.id.countDistinct()
    val followersCount = FollowersInUsers.userId.countDistinct()
    val followingCount = FollowersInUsers.following[FollowersInUsers.targetId].countDistinct()
    val followersIn = FollowersInUsers.followersIn[FollowersInUsers.userId].countDistinct()
    val followingIn = FollowersInUsers.followingIn[FollowersInUsers.targetId].countDistinct()

    val usernameRegex = Regex("[a-zA-Z0-9_]+")

    override val primaryKey = PrimaryKey(id)

    fun generateId(): String {
        val candidate = String.generateId()
        return if (select { id eq candidate }.count() > 0) generateId() else candidate
    }

    fun toUser(
        row: ResultRow,
    ) = User(
        row[id],
        row.getOrNull(displayName),
        row.getOrNull(username),
        row.getOrNull(email),
        row.getOrNull(password),
        row.getOrNull(biography),
        row.getOrNull(avatar),
        row.getOrNull(birthdate)?.toLocalDate(),
        row.getOrNull(joinDate)?.toInstant(),
        row.getOrNull(lastActive)?.toInstant(),
        row.getOrNull(personal),
        row.getOrNull(emailVerified),
        row.getOrNull(verified),
        row.getOrNull(banned),
        row.getOrNull(postsCount),
        row.getOrNull(followersCount),
        row.getOrNull(followingCount),
        row.getOrNull(followersIn)?.let { it >= 1L },
        row.getOrNull(followingIn)?.let { it >= 1L }
    )

    fun customJoin(viewedBy: String, additionalFields: List<Expression<*>> = listOf()): FieldSet {
        return Users.join(Posts, JoinType.LEFT, id, Posts.userId)
            .join(FollowersInUsers, JoinType.LEFT, id, FollowersInUsers.targetId)
            .join(
                FollowersInUsers.following,
                JoinType.LEFT,
                id,
                FollowersInUsers.following[FollowersInUsers.userId]
            )
            .join(
                FollowersInUsers.followersIn,
                JoinType.LEFT,
                id,
                FollowersInUsers.followersIn[FollowersInUsers.targetId]
            ) {
                FollowersInUsers.followersIn[FollowersInUsers.userId] eq viewedBy and
                        (FollowersInUsers.followersIn[FollowersInUsers.accepted] eq true)
            }
            .join(
                FollowersInUsers.followingIn,
                JoinType.LEFT,
                id,
                FollowersInUsers.followingIn[FollowersInUsers.userId]
            ) {
                FollowersInUsers.followingIn[FollowersInUsers.targetId] eq viewedBy and
                        (FollowersInUsers.followingIn[FollowersInUsers.accepted] eq true)
            }
            .slice(
                additionalFields +
                        id +
                        displayName +
                        username +
                        biography +
                        avatar +
                        birthdate +
                        joinDate +
                        personal +
                        verified +
                        banned +
                        postsCount +
                        followersCount +
                        followingCount +
                        followersIn +
                        followingIn
            )
    }
}

@Serializable
data class UserAuthorize(val client_id: String, val client_secret: String, val code: String)

//@Serializable
//data class UserAuthorizeRequest(val client: Client, val user: User)

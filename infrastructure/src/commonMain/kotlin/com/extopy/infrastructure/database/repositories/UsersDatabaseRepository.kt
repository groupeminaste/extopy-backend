package com.extopy.infrastructure.database.repositories

import com.extopy.domain.models.application.Pagination
import com.extopy.domain.models.users.CreateUserPayload
import com.extopy.domain.models.users.UpdateUserPayload
import com.extopy.domain.models.users.User
import com.extopy.domain.repositories.UsersRepository
import com.extopy.infrastructure.database.TransactionManager
import com.extopy.infrastructure.database.tables.FollowersInUsers
import com.extopy.infrastructure.database.tables.Posts
import com.extopy.infrastructure.database.tables.Users
import org.jetbrains.exposed.v1.core.Expression
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.Query
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import kotlin.time.Clock
import kotlin.uuid.Uuid

class UsersDatabaseRepository(
    private val transactionManager: TransactionManager,
) : UsersRepository {

    init {
        transactionManager.transaction {
            // Every read of a user joins its posts and its follow links in to count them, so those
            // two have to be there as well.
            SchemaUtils.create(Users, Posts, FollowersInUsers)
        }
    }

    override suspend fun list(pagination: Pagination, viewedBy: Uuid): List<User> =
        transactionManager.suspendTransaction {
            customJoin(viewedBy)
                .groupBy(Users.id)
                .search(pagination.search)
                .orderBy(Users.joinDate to SortOrder.DESC)
                .limit(pagination.limit.toInt())
                .offset(pagination.offset)
                .map(Users::toUser)
        }

    override suspend fun get(id: Uuid, viewedBy: Uuid): User? =
        transactionManager.suspendTransaction {
            customJoin(viewedBy)
                .where { Users.id eq id }
                .groupBy(Users.id)
                .map(Users::toUser)
                .firstOrNull()
        }

    override suspend fun getForUsernameOrEmail(usernameOrEmail: String, includePassword: Boolean): User? =
        transactionManager.suspendTransaction {
            Users
                .selectAll()
                .where { Users.username eq usernameOrEmail or (Users.email eq usernameOrEmail) }
                .map { Users.toUser(it, includePassword) }
                .firstOrNull()
        }

    override suspend fun create(payload: CreateUserPayload): User? =
        transactionManager.suspendTransaction {
            val now = Clock.System.now()
            Users.insert {
                it[displayName] = payload.displayName
                it[username] = payload.username
                it[email] = payload.email
                it[password] = payload.password
                it[biography] = User.DEFAULT_BIOGRAPHY
                it[birthdate] = payload.birthdate
                it[joinDate] = now
                it[lastActive] = now
            }.resultedValues?.map(Users::toUser)?.firstOrNull()
        }

    override suspend fun update(id: Uuid, payload: UpdateUserPayload): Boolean {
        // An update with nothing to set is not a statement any database will take, and a caller
        // sending an empty body has had its way regardless.
        if (payload == EMPTY_UPDATE) return true
        return transactionManager.suspendTransaction {
            Users.update({ Users.id eq id }) { statement ->
                payload.username?.let { statement[Users.username] = it }
                payload.displayName?.let { statement[Users.displayName] = it }
                payload.password?.let { statement[Users.password] = it }
                payload.biography?.let { statement[Users.biography] = it }
                payload.avatar?.let { statement[Users.avatar] = it }
                payload.personal?.let { statement[Users.personal] = it }
            }
        } == 1
    }

    /**
     * Every user is read through the same join, so that the counters and the two follow flags are
     * always there. The two flags depend on who is reading, which is why [viewedBy] reaches this far
     * down: they say whether that account follows this one, and whether this one follows it back.
     */
    private fun customJoin(viewedBy: Uuid, additionalFields: List<Expression<*>> = listOf()): Query =
        Users.join(Posts, JoinType.LEFT, Users.id, Posts.userId)
            .join(FollowersInUsers, JoinType.LEFT, Users.id, FollowersInUsers.targetId)
            .join(
                FollowersInUsers.following,
                JoinType.LEFT,
                Users.id,
                FollowersInUsers.following[FollowersInUsers.userId]
            )
            .join(
                FollowersInUsers.followersIn,
                JoinType.LEFT,
                Users.id,
                FollowersInUsers.followersIn[FollowersInUsers.targetId]
            ) {
                FollowersInUsers.followersIn[FollowersInUsers.userId] eq viewedBy and
                        (FollowersInUsers.followersIn[FollowersInUsers.accepted] eq true)
            }
            .join(
                FollowersInUsers.followingIn,
                JoinType.LEFT,
                Users.id,
                FollowersInUsers.followingIn[FollowersInUsers.userId]
            ) {
                FollowersInUsers.followingIn[FollowersInUsers.targetId] eq viewedBy and
                        (FollowersInUsers.followingIn[FollowersInUsers.accepted] eq true)
            }
            .select(
                additionalFields +
                        Users.id +
                        Users.displayName +
                        Users.username +
                        Users.biography +
                        Users.avatar +
                        Users.birthdate +
                        Users.joinDate +
                        Users.personal +
                        Users.verified +
                        Users.banned +
                        Users.postsCount +
                        Users.followersCount +
                        Users.followingCount +
                        Users.followersIn +
                        Users.followingIn
            )

    /**
     * Matches any of the words of the search against the username, the display name and the
     * biography. The wildcards of `LIKE` are escaped first, so that a search for `100%` is a search
     * and not a match-everything.
     */
    private fun Query.search(search: String?): Query {
        if (search == null) return this
        val escaped = search.replace("%", "\\%").replace("_", "\\_")
        return andWhere {
            escaped.split(" ")
                .map { Users.username like "%$it%" or (Users.displayName like "%$it%") or (Users.biography like "%$it%") }
                .fold(Op.FALSE as Op<Boolean>) { a, b -> a or b }
        }
    }

    companion object {

        private val EMPTY_UPDATE = UpdateUserPayload()

    }

}

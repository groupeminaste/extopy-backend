package me.nathanfallet.extopy.database.users

import kotlinx.datetime.Clock
import me.nathanfallet.extopy.database.posts.Posts
import me.nathanfallet.extopy.models.application.SearchOptions
import me.nathanfallet.extopy.models.users.CreateUserPayload
import me.nathanfallet.extopy.models.users.UpdateUserPayload
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.extopy.repositories.users.IUsersRepository
import me.nathanfallet.surexposed.database.IDatabase
import me.nathanfallet.usecases.context.IContext
import me.nathanfallet.usecases.pagination.IPaginationOptions
import me.nathanfallet.usecases.pagination.Pagination
import org.jetbrains.exposed.sql.*

class UsersDatabaseRepository(
    private val database: IDatabase,
) : IUsersRepository {

    init {
        database.transaction {
            SchemaUtils.create(Users)
        }
    }

    override suspend fun list(pagination: Pagination, context: IContext?): List<User> {
        if (context !is UserContext) return emptyList()
        return database.suspendedTransaction {
            customJoin(context.userId)
                .groupBy(Users.id)
                .andWhere(pagination.options)
                .orderBy(Users.joinDate to SortOrder.DESC)
                .limit(pagination.limit.toInt(), pagination.offset)
                .map(Users::toUser)
        }
    }

    override suspend fun get(id: String, context: IContext?): User? {
        if (context !is UserContext) return null
        return database.suspendedTransaction {
            customJoin(context.userId)
                .where { Users.id eq id }
                .groupBy(Users.id)
                .map(Users::toUser)
                .singleOrNull()
        }
    }

    override suspend fun getForUsernameOrEmail(username: String, includePassword: Boolean): User? =
        database.suspendedTransaction {
            Users
                .selectAll()
                .where { Users.username eq username or (Users.email eq username) }
                .map {
                    Users.toUser(it, includePassword)
                }
                .singleOrNull()
        }

    override suspend fun create(payload: CreateUserPayload, context: IContext?): User? =
        database.suspendedTransaction {
            Users.insert {
                it[id] = generateId()
                it[displayName] = payload.displayName
                it[username] = payload.username
                it[email] = payload.email
                it[password] = payload.password
                it[biography] = "Hello, I'm new on Extopy!"
                it[birthdate] = payload.birthdate.toString()
                it[joinDate] = Clock.System.now().toString()
                it[lastActive] = Clock.System.now().toString()
            }.resultedValues?.map(Users::toUser)?.singleOrNull()
        }

    override suspend fun update(id: String, payload: UpdateUserPayload, context: IContext?): Boolean =
        database.suspendedTransaction {
            Users.update({ Users.id eq id }) {
                payload.username?.let { username ->
                    it[Users.username] = username
                }
                payload.displayName?.let { displayName ->
                    it[Users.displayName] = displayName
                }
                payload.password?.let { password ->
                    it[Users.password] = password
                }
                payload.biography?.let { biography ->
                    it[Users.biography] = biography
                }
                payload.avatar?.let { avatar ->
                    it[Users.avatar] = avatar
                }
                payload.personal?.let { personal ->
                    it[Users.personal] = personal
                }
            }
        } == 1

    override suspend fun delete(id: String, context: IContext?): Boolean =
        TODO("Not yet implemented")

    private fun customJoin(viewedBy: String, additionalFields: List<Expression<*>> = listOf()): Query =
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

    private fun Query.andWhere(options: IPaginationOptions?): Query = when (options) {
        is SearchOptions -> {
            val likeString = options.search
                .replace("%", "\\%")
                .replace("_", "\\_")
            andWhere {
                likeString.split(" ").map {
                    Users.username like "%$it%" or
                            (Users.displayName like "%$it%") or
                            (Users.biography like "%$it%")
                }.fold(Op.FALSE as Op<Boolean>) { a, b -> a or b }
            }

        }

        else -> this
    }

}

package me.nathanfallet.extopy.database.users

import me.nathanfallet.extopy.models.users.FollowerInUser
import me.nathanfallet.extopy.models.users.FollowerInUserContext
import me.nathanfallet.extopy.repositories.users.IFollowersInUsersRepository
import me.nathanfallet.surexposed.database.IDatabase
import me.nathanfallet.usecases.context.IContext
import me.nathanfallet.usecases.pagination.Pagination
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class FollowersInUsersDatabaseRepository(
    private val database: IDatabase,
) : IFollowersInUsersRepository {

    init {
        database.transaction {
            SchemaUtils.create(FollowersInUsers)
        }
    }

    override suspend fun list(pagination: Pagination, parentId: String, context: IContext?): List<FollowerInUser> =
        database.suspendedTransaction {
            customFollowersJoin()
                .where { FollowersInUsers.targetId eq parentId and (FollowersInUsers.accepted eq true) }
                .limit(pagination.limit.toInt(), pagination.offset)
                .map { FollowersInUsers.toFollowerInUser(it, Users.toUser(it), null) }
        }

    override suspend fun listFollowing(
        pagination: Pagination,
        parentId: String,
        context: IContext?,
    ): List<FollowerInUser> =
        database.suspendedTransaction {
            customFollowingJoin()
                .where { FollowersInUsers.userId eq parentId and (FollowersInUsers.accepted eq true) }
                .limit(pagination.limit.toInt(), pagination.offset)
                .map { FollowersInUsers.toFollowerInUser(it, null, Users.toUser(it)) }
        }

    override suspend fun create(payload: Unit, parentId: String, context: IContext?): FollowerInUser? {
        if (context !is FollowerInUserContext) return null
        return database.suspendedTransaction {
            FollowersInUsers.insert {
                it[userId] = context.userId
                it[targetId] = parentId
                it[accepted] = context.isTargetPublic
            }.resultedValues?.map { FollowersInUsers.toFollowerInUser(it, null, null) }?.singleOrNull()
        }
    }

    override suspend fun delete(id: String, parentId: String, context: IContext?): Boolean =
        database.suspendedTransaction {
            FollowersInUsers.deleteWhere {
                userId eq id and (targetId eq parentId)
            }
        } == 1

    private fun customFollowersJoin(): Query =
        FollowersInUsers
            .join(Users, JoinType.LEFT, FollowersInUsers.userId, Users.id)
            .customUsersFollowersSlice()

    private fun customFollowingJoin(): Query =
        FollowersInUsers
            .join(Users, JoinType.LEFT, FollowersInUsers.targetId, Users.id)
            .customUsersFollowersSlice()

    private fun ColumnSet.customUsersFollowersSlice(additionalFields: List<Expression<*>> = listOf()): Query =
        select(
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

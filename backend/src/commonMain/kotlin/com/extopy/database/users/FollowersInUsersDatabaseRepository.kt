package com.extopy.database.users

import com.extopy.models.users.FollowerInUser
import com.extopy.models.users.FollowerInUserContext
import com.extopy.repositories.users.IFollowersInUsersRepository
import dev.kaccelero.database.IDatabase
import dev.kaccelero.database.eq
import dev.kaccelero.database.set
import dev.kaccelero.models.IContext
import dev.kaccelero.models.UUID
import dev.kaccelero.repositories.Pagination
import org.jetbrains.exposed.sql.*

class FollowersInUsersDatabaseRepository(
    private val database: IDatabase,
) : IFollowersInUsersRepository {

    init {
        database.transaction {
            SchemaUtils.create(FollowersInUsers)
        }
    }

    override suspend fun list(pagination: Pagination, parentId: UUID, context: IContext?): List<FollowerInUser> =
        database.suspendedTransaction {
            customFollowersJoin()
                .where { FollowersInUsers.targetId eq parentId and (FollowersInUsers.accepted eq true) }
                .limit(pagination.limit.toInt(), pagination.offset)
                .map { FollowersInUsers.toFollowerInUser(it, Users.toUser(it), null) }
        }

    override suspend fun listFollowing(
        pagination: Pagination,
        parentId: UUID,
        context: IContext?,
    ): List<FollowerInUser> =
        database.suspendedTransaction {
            customFollowingJoin()
                .where { FollowersInUsers.userId eq parentId and (FollowersInUsers.accepted eq true) }
                .limit(pagination.limit.toInt(), pagination.offset)
                .map { FollowersInUsers.toFollowerInUser(it, null, Users.toUser(it)) }
        }

    override suspend fun create(payload: Unit, parentId: UUID, context: IContext?): FollowerInUser? {
        if (context !is FollowerInUserContext) return null
        return database.suspendedTransaction {
            FollowersInUsers.insert {
                it[userId] = context.userId
                it[targetId] = parentId
                it[accepted] = context.isTargetPublic
            }.resultedValues?.map { FollowersInUsers.toFollowerInUser(it, null, null) }?.singleOrNull()
        }
    }

    override suspend fun delete(id: UUID, parentId: UUID, context: IContext?): Boolean =
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

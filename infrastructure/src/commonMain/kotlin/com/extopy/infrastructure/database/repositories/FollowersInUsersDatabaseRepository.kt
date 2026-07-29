package com.extopy.infrastructure.database.repositories

import com.extopy.domain.models.application.Pagination
import com.extopy.domain.models.users.FollowerInUser
import com.extopy.domain.repositories.FollowersInUsersRepository
import com.extopy.infrastructure.database.TransactionManager
import com.extopy.infrastructure.database.tables.FollowersInUsers
import com.extopy.infrastructure.database.tables.Users
import org.jetbrains.exposed.v1.core.ColumnSet
import org.jetbrains.exposed.v1.core.Expression
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Query
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import kotlin.uuid.Uuid

class FollowersInUsersDatabaseRepository(
    private val transactionManager: TransactionManager,
) : FollowersInUsersRepository {

    init {
        transactionManager.transaction {
            SchemaUtils.create(FollowersInUsers)
        }
    }

    override suspend fun list(pagination: Pagination, targetId: Uuid): List<FollowerInUser> =
        transactionManager.suspendTransaction {
            // The follower is the one being listed, so the join hangs off `userId`.
            FollowersInUsers
                .join(Users, JoinType.LEFT, FollowersInUsers.userId, Users.id)
                .customFollowersSlice()
                .where { FollowersInUsers.targetId eq targetId and (FollowersInUsers.accepted eq true) }
                .limit(pagination.limit.toInt())
                .offset(pagination.offset)
                .map { FollowersInUsers.toFollowerInUser(it, user = Users.toUser(it)) }
        }

    override suspend fun listFollowing(pagination: Pagination, userId: Uuid): List<FollowerInUser> =
        transactionManager.suspendTransaction {
            // The account being followed is the one being listed, so the join hangs off `targetId`.
            FollowersInUsers
                .join(Users, JoinType.LEFT, FollowersInUsers.targetId, Users.id)
                .customFollowersSlice()
                .where { FollowersInUsers.userId eq userId and (FollowersInUsers.accepted eq true) }
                .limit(pagination.limit.toInt())
                .offset(pagination.offset)
                .map { FollowersInUsers.toFollowerInUser(it, target = Users.toUser(it)) }
        }

    override suspend fun create(userId: Uuid, targetId: Uuid, accepted: Boolean): FollowerInUser? =
        transactionManager.suspendTransaction {
            FollowersInUsers.insert {
                it[FollowersInUsers.userId] = userId
                it[FollowersInUsers.targetId] = targetId
                it[FollowersInUsers.accepted] = accepted
            }.resultedValues?.map { FollowersInUsers.toFollowerInUser(it) }?.firstOrNull()
        }

    override suspend fun delete(userId: Uuid, targetId: Uuid): Boolean =
        transactionManager.suspendTransaction {
            FollowersInUsers.deleteWhere {
                FollowersInUsers.userId eq userId and (FollowersInUsers.targetId eq targetId)
            }
        } == 1

    private fun ColumnSet.customFollowersSlice(additionalFields: List<Expression<*>> = listOf()): Query =
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

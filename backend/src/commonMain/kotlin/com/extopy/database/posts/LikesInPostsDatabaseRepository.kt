package com.extopy.database.posts

import com.extopy.database.users.Users
import com.extopy.models.posts.LikeInPost
import com.extopy.models.users.UserContext
import dev.kaccelero.database.IDatabase
import dev.kaccelero.database.eq
import dev.kaccelero.database.set
import dev.kaccelero.models.IContext
import dev.kaccelero.models.UUID
import dev.kaccelero.repositories.IChildModelSuspendRepository
import dev.kaccelero.repositories.Pagination
import org.jetbrains.exposed.sql.*

class LikesInPostsDatabaseRepository(
    private val database: IDatabase,
) : IChildModelSuspendRepository<LikeInPost, UUID, Unit, Unit, UUID> {

    init {
        database.transaction {
            SchemaUtils.create(LikesInPosts)
        }
    }

    override suspend fun list(pagination: Pagination, parentId: UUID, context: IContext?): List<LikeInPost> =
        database.suspendedTransaction {
            customJoin()
                .where { LikesInPosts.postId eq parentId }
                .limit(pagination.limit.toInt())
                .offset(pagination.offset)
                .map { LikesInPosts.toLikeInPost(it, null, Users.toUser(it)) }
        }

    override suspend fun create(payload: Unit, parentId: UUID, context: IContext?): LikeInPost? {
        if (context !is UserContext) return null
        return database.suspendedTransaction {
            LikesInPosts.insert {
                it[postId] = parentId
                it[userId] = context.userId
            }.resultedValues?.map { LikesInPosts.toLikeInPost(it, null, null) }?.singleOrNull()
        }
    }

    override suspend fun delete(id: UUID, parentId: UUID, context: IContext?): Boolean =
        database.suspendedTransaction {
            LikesInPosts.deleteWhere {
                postId eq parentId and (userId eq id)
            }
        } == 1

    private fun customJoin(additionalFields: List<Expression<*>> = listOf()): Query =
        LikesInPosts
            .join(Users, JoinType.LEFT, LikesInPosts.userId, Users.id)
            .select(
                additionalFields +
                        LikesInPosts.postId +
                        LikesInPosts.userId +
                        Users.id +
                        Users.displayName +
                        Users.username +
                        Users.avatar +
                        Users.verified
            )

}

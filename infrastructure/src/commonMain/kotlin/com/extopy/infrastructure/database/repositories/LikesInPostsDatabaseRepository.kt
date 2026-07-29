package com.extopy.infrastructure.database.repositories

import com.extopy.domain.models.application.Pagination
import com.extopy.domain.models.posts.LikeInPost
import com.extopy.domain.repositories.LikesInPostsRepository
import com.extopy.infrastructure.database.TransactionManager
import com.extopy.infrastructure.database.tables.LikesInPosts
import com.extopy.infrastructure.database.tables.Users
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

class LikesInPostsDatabaseRepository(
    private val transactionManager: TransactionManager,
) : LikesInPostsRepository {

    init {
        transactionManager.transaction {
            // A like is only ever listed with the account that left it, which is joined in.
            SchemaUtils.create(LikesInPosts, Users)
        }
    }

    override suspend fun list(pagination: Pagination, postId: Uuid): List<LikeInPost> =
        transactionManager.suspendTransaction {
            customJoin()
                .where { LikesInPosts.postId eq postId }
                .limit(pagination.limit.toInt())
                .offset(pagination.offset)
                .map { LikesInPosts.toLikeInPost(it, user = Users.toUser(it)) }
        }

    override suspend fun create(postId: Uuid, userId: Uuid): LikeInPost? =
        transactionManager.suspendTransaction {
            LikesInPosts.insert {
                it[LikesInPosts.postId] = postId
                it[LikesInPosts.userId] = userId
            }.resultedValues?.map { LikesInPosts.toLikeInPost(it) }?.firstOrNull()
        }

    override suspend fun delete(postId: Uuid, userId: Uuid): Boolean =
        transactionManager.suspendTransaction {
            LikesInPosts.deleteWhere {
                LikesInPosts.postId eq postId and (LikesInPosts.userId eq userId)
            }
        } == 1

    /**
     * A like is only ever listed to show who left it, so the author comes along with it.
     */
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

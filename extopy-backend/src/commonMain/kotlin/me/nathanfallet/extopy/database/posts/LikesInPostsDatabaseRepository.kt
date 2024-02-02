package me.nathanfallet.extopy.database.posts

import me.nathanfallet.extopy.database.users.Users
import me.nathanfallet.extopy.models.posts.LikeInPost
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.surexposed.database.IDatabase
import me.nathanfallet.usecases.context.IContext
import me.nathanfallet.usecases.models.repositories.IChildModelSuspendRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class LikesInPostsDatabaseRepository(
    private val database: IDatabase,
) : IChildModelSuspendRepository<LikeInPost, String, Unit, Unit, String> {

    init {
        database.transaction {
            SchemaUtils.create(LikesInPosts)
        }
    }

    override suspend fun list(limit: Long, offset: Long, parentId: String, context: IContext?): List<LikeInPost> =
        database.suspendedTransaction {
            customJoin()
                .where { LikesInPosts.postId eq parentId }
                .limit(limit.toInt(), offset)
                .map { LikesInPosts.toLikeInPost(it, null, Users.toUser(it)) }
        }

    override suspend fun create(payload: Unit, parentId: String, context: IContext?): LikeInPost? {
        if (context !is UserContext) return null
        return database.suspendedTransaction {
            LikesInPosts.insert {
                it[postId] = parentId
                it[userId] = context.userId
            }.resultedValues?.map { LikesInPosts.toLikeInPost(it, null, null) }?.singleOrNull()
        }
    }

    override suspend fun delete(id: String, parentId: String, context: IContext?): Boolean =
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

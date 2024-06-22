package com.extopy.database.posts

import kotlinx.datetime.Clock
import com.extopy.database.users.FollowersInUsers
import com.extopy.database.users.Users
import com.extopy.models.application.SearchOptions
import com.extopy.models.posts.Post
import com.extopy.models.posts.PostPayload
import com.extopy.models.users.UserContext
import com.extopy.repositories.posts.IPostsRepository
import dev.kaccelero.database.IDatabase
import dev.kaccelero.models.IContext
import dev.kaccelero.repositories.IPaginationOptions
import dev.kaccelero.repositories.Pagination
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class PostsDatabaseRepository(
    private val database: IDatabase,
) : IPostsRepository {

    init {
        database.transaction {
            SchemaUtils.create(Posts)
        }
    }

    override suspend fun list(pagination: Pagination, context: IContext?): List<Post> {
        if (context !is UserContext) return emptyList()
        return database.suspendedTransaction {
            customJoin(context.userId)
                .groupBy(Posts.id)
                .andWhere(pagination.options)
                .orderBy(Posts.published to SortOrder.DESC)
                .limit(pagination.limit.toInt(), pagination.offset)
                .map { Posts.toPost(it, Users.toUser(it)) }
        }
    }

    override suspend fun listDefault(pagination: Pagination, context: UserContext): List<Post> =
        database.suspendedTransaction {
            customJoinColumnSet(context.userId)
                .join(
                    FollowersInUsers,
                    JoinType.LEFT,
                    Posts.userId,
                    FollowersInUsers.targetId
                )
                .customPostsSlice()
                .where {
                    Posts.userId eq context.userId or
                            (FollowersInUsers.userId eq context.userId and
                                    (FollowersInUsers.accepted eq true))
                }
                .groupBy(Posts.id)
                .orderBy(Posts.published to SortOrder.DESC)
                .limit(pagination.limit.toInt(), pagination.offset)
                .map { Posts.toPost(it, Users.toUser(it)) }
        }

    override suspend fun listTrends(pagination: Pagination, context: UserContext): List<Post> =
        database.suspendedTransaction {
            customJoin(context.userId)
                .groupBy(Posts.id)
                .orderBy(Posts.trendsCount to SortOrder.DESC)
                .limit(pagination.limit.toInt(), pagination.offset)
                .map { Posts.toPost(it, Users.toUser(it)) }
        }

    override suspend fun listUserPosts(userId: String, pagination: Pagination, context: UserContext): List<Post> =
        database.suspendedTransaction {
            customJoin(context.userId)
                .where { Posts.userId eq userId }
                .groupBy(Posts.id)
                .orderBy(Posts.published to SortOrder.DESC)
                .limit(pagination.limit.toInt(), pagination.offset)
                .map { Posts.toPost(it, Users.toUser(it)) }
        }

    override suspend fun listReplies(postId: String, pagination: Pagination, context: UserContext): List<Post> =
        database.suspendedTransaction {
            customJoin(context.userId)
                .where { Posts.repliedToId eq postId }
                .groupBy(Posts.id)
                .orderBy(Posts.published to SortOrder.DESC)
                .limit(pagination.limit.toInt(), pagination.offset)
                .map { Posts.toPost(it, Users.toUser(it)) }
        }

    override suspend fun get(id: String, context: IContext?): Post? {
        if (context !is UserContext) return null
        return database.suspendedTransaction {
            customJoin(context.userId)
                .where { Posts.id eq id }
                .groupBy(Posts.id)
                .map { Posts.toPost(it, Users.toUser(it)) }
                .singleOrNull()
        }
    }

    override suspend fun create(payload: PostPayload, context: IContext?): Post? {
        if (context !is UserContext) return null
        val id = database.suspendedTransaction {
            val id = Posts.generateId()
            Posts.insert {
                it[Posts.id] = id
                it[userId] = context.userId
                it[repliedToId] = payload.repliedToId
                it[repostOfId] = payload.repostOfId
                it[body] = payload.body
                it[published] = Clock.System.now().toString()
                it[edited] = null
                it[expiration] = Clock.System.now().toString()
                it[visibility] = ""
            }
            id
        }
        return get(id, context)
    }

    override suspend fun update(id: String, payload: PostPayload, context: IContext?): Boolean =
        database.suspendedTransaction {
            Posts.update({ Posts.id eq id }) {
                it[body] = payload.body
                it[edited] = Clock.System.now().toString()
            }
        } == 1

    override suspend fun delete(id: String, context: IContext?): Boolean =
        database.suspendedTransaction {
            Posts.deleteWhere {
                Posts.id eq id
            }
        } == 1

    private fun customJoin(viewedBy: String): Query =
        customJoinColumnSet(viewedBy).customPostsSlice()

    private fun customJoinColumnSet(viewedBy: String): ColumnSet =
        Posts.join(Users, JoinType.INNER, Posts.userId, Users.id)
            .join(LikesInPosts, JoinType.LEFT, Posts.id, LikesInPosts.postId)
            .join(Posts.replies, JoinType.LEFT, Posts.id, Posts.replies[Posts.repliedToId])
            .join(Posts.reposts, JoinType.LEFT, Posts.id, Posts.reposts[Posts.repostOfId])
            .join(
                LikesInPosts.likesIn,
                JoinType.LEFT,
                Posts.id,
                LikesInPosts.likesIn[LikesInPosts.postId]
            ) { LikesInPosts.likesIn[LikesInPosts.userId] eq viewedBy }

    private fun ColumnSet.customPostsSlice(additionalFields: List<Expression<*>> = listOf()): Query =
        select(
            Posts.columns +
                    Users.id +
                    Users.displayName +
                    Users.username +
                    Users.avatar +
                    Users.verified +
                    Posts.likesCount +
                    Posts.repliesCount +
                    Posts.repostsCount +
                    Posts.likesIn +
                    additionalFields
        )

    private fun Query.andWhere(options: IPaginationOptions?): Query = when (options) {
        is SearchOptions -> {
            val likeString = options.search
                .replace("%", "\\%")
                .replace("_", "\\_")
            andWhere {
                likeString.split(" ")
                    .map { Posts.body like "%$it%" }
                    .fold(Op.FALSE as Op<Boolean>) { a, b -> a or b }
            }

        }

        else -> this
    }

}

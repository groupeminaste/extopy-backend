package com.extopy.infrastructure.database.repositories

import com.extopy.domain.models.application.Pagination
import com.extopy.domain.models.posts.Post
import com.extopy.domain.models.posts.PostPayload
import com.extopy.domain.repositories.PostsRepository
import com.extopy.infrastructure.database.TransactionManager
import com.extopy.infrastructure.database.tables.FollowersInUsers
import com.extopy.infrastructure.database.tables.LikesInPosts
import com.extopy.infrastructure.database.tables.Posts
import com.extopy.infrastructure.database.tables.Users
import org.jetbrains.exposed.v1.core.ColumnSet
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
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.update
import kotlin.time.Clock
import kotlin.uuid.Uuid

class PostsDatabaseRepository(
    private val transactionManager: TransactionManager,
) : PostsRepository {

    init {
        transactionManager.transaction {
            // Every read of a post joins its author, its likes and what points back at it, so those
            // have to be there as well.
            SchemaUtils.create(Posts, Users, LikesInPosts, FollowersInUsers)
        }
    }

    override suspend fun list(pagination: Pagination, viewedBy: Uuid): List<Post> =
        transactionManager.suspendTransaction {
            customJoin(viewedBy)
                .groupBy(Posts.id)
                .search(pagination.search)
                .orderBy(Posts.publishedAt to SortOrder.DESC)
                .paginate(pagination)
                .map { Posts.toPost(it, Users.toUser(it)) }
        }

    override suspend fun listDefault(pagination: Pagination, viewedBy: Uuid): List<Post> =
        transactionManager.suspendTransaction {
            customJoinColumnSet(viewedBy)
                .join(FollowersInUsers, JoinType.LEFT, Posts.userId, FollowersInUsers.targetId)
                .customPostsSlice()
                .where {
                    Posts.userId eq viewedBy or
                            (FollowersInUsers.userId eq viewedBy and (FollowersInUsers.accepted eq true))
                }
                .groupBy(Posts.id)
                .orderBy(Posts.publishedAt to SortOrder.DESC)
                .paginate(pagination)
                .map { Posts.toPost(it, Users.toUser(it)) }
        }

    override suspend fun listTrends(pagination: Pagination, viewedBy: Uuid): List<Post> =
        transactionManager.suspendTransaction {
            customJoin(viewedBy)
                .groupBy(Posts.id)
                .orderBy(Posts.trendsCount to SortOrder.DESC)
                .paginate(pagination)
                .map { Posts.toPost(it, Users.toUser(it)) }
        }

    override suspend fun listUserPosts(userId: Uuid, pagination: Pagination, viewedBy: Uuid): List<Post> =
        transactionManager.suspendTransaction {
            customJoin(viewedBy)
                .where { Posts.userId eq userId }
                .groupBy(Posts.id)
                .orderBy(Posts.publishedAt to SortOrder.DESC)
                .paginate(pagination)
                .map { Posts.toPost(it, Users.toUser(it)) }
        }

    override suspend fun listReplies(postId: Uuid, pagination: Pagination, viewedBy: Uuid): List<Post> =
        transactionManager.suspendTransaction {
            customJoin(viewedBy)
                .where { Posts.repliedToId eq postId }
                .groupBy(Posts.id)
                .orderBy(Posts.publishedAt to SortOrder.DESC)
                .paginate(pagination)
                .map { Posts.toPost(it, Users.toUser(it)) }
        }

    override suspend fun get(id: Uuid, viewedBy: Uuid): Post? =
        transactionManager.suspendTransaction {
            customJoin(viewedBy)
                .where { Posts.id eq id }
                .groupBy(Posts.id)
                .map { Posts.toPost(it, Users.toUser(it)) }
                .firstOrNull()
        }

    override suspend fun create(payload: PostPayload, userId: Uuid): Post? {
        val id = transactionManager.suspendTransaction {
            val now = Clock.System.now()
            Posts.insertAndGetId {
                it[Posts.userId] = userId
                it[repliedToId] = payload.repliedToId
                it[repostOfId] = payload.repostOfId
                it[body] = payload.body
                it[publishedAt] = now
                // Note: posts do not expire yet, and nothing reads these two back. They are written
                // the way they always were so that the columns stay non-null.
                it[expiresAt] = now
                it[visibility] = ""
            }
        }
        return get(id.value, userId)
    }

    override suspend fun update(id: Uuid, payload: PostPayload): Boolean =
        transactionManager.suspendTransaction {
            Posts.update({ Posts.id eq id }) {
                it[body] = payload.body
                it[editedAt] = Clock.System.now()
            }
        } == 1

    override suspend fun delete(id: Uuid): Boolean =
        transactionManager.suspendTransaction {
            Posts.deleteWhere { Posts.id eq id }
        } == 1

    private fun customJoin(viewedBy: Uuid): Query =
        customJoinColumnSet(viewedBy).customPostsSlice()

    /**
     * Every post is read through the same join: its author, the likes and the replies and the
     * reposts it collected, plus the like of the account doing the reading, which is what the
     * `likesIn` flag is counted from.
     */
    private fun customJoinColumnSet(viewedBy: Uuid): ColumnSet =
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

    private fun Query.paginate(pagination: Pagination): Query =
        limit(pagination.limit.toInt()).offset(pagination.offset)

    /**
     * Matches any of the words of the search against the body. The wildcards of `LIKE` are escaped
     * first, so that a search for `100%` is a search and not a match-everything.
     */
    private fun Query.search(search: String?): Query {
        if (search == null) return this
        val escaped = search.replace("%", "\\%").replace("_", "\\_")
        return andWhere {
            escaped.split(" ")
                .map { Posts.body like "%$it%" }
                .fold(Op.FALSE as Op<Boolean>) { a, b -> a or b }
        }
    }

}

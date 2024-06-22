package com.extopy.database.posts

import com.extopy.database.Database
import com.extopy.database.users.UsersDatabaseRepository
import com.extopy.models.posts.PostPayload
import com.extopy.models.users.CreateUserPayload
import com.extopy.models.users.UserContext
import com.extopy.repositories.posts.IPostsRepository
import dev.kaccelero.database.IDatabase
import dev.kaccelero.models.UUID
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.selectAll
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class PostsDatabaseRepositoryTest {

    private fun createRepository(database: IDatabase): IPostsRepository {
        LikesInPostsDatabaseRepository(database)
        return PostsDatabaseRepository(database)
    }

    @Test
    fun createPost() = runBlocking {
        val database = Database(protocol = "h2", name = "createPost")
        val repository = createRepository(database)
        val user = UsersDatabaseRepository(database).create(
            CreateUserPayload(
                "username", "displayName", "email", "password",
                LocalDate(2002, 12, 24)
            )
        ) ?: fail("Unable to create user")
        val post = repository.create(
            PostPayload("body", null, null),
            UserContext(user.id)
        )
        val postFromDatabase = database.suspendedTransaction {
            Posts
                .selectAll()
                .map {
                    Posts.toPost(it)
                }
                .singleOrNull()
        }
        assertEquals(postFromDatabase?.id, post?.id)
        assertEquals(postFromDatabase?.userId, post?.userId)
        assertEquals(postFromDatabase?.repostOfId, post?.repostOfId)
        assertEquals(postFromDatabase?.repliedToId, post?.repliedToId)
        assertEquals(postFromDatabase?.body, post?.body)
        assertEquals(postFromDatabase?.publishedAt, post?.publishedAt)
        assertEquals(postFromDatabase?.editedAt, post?.editedAt)
        assertEquals(postFromDatabase?.expiresAt, post?.expiresAt)
        assertEquals(postFromDatabase?.visibility, post?.visibility)
        assertEquals(postFromDatabase?.userId, user.id)
        assertEquals(postFromDatabase?.body, "body")
        assertEquals(user.id, post?.user?.id)
        assertEquals(user.username, post?.user?.username)
        assertEquals(user.displayName, post?.user?.displayName)
    }

    @Test
    fun createPostNoContext() = runBlocking {
        val database = Database(protocol = "h2", name = "createPostNoContext")
        val repository = createRepository(database)
        assertEquals(
            null, repository.create(
                PostPayload("body", null, null)
            )
        )
    }

    @Test
    fun getPost() = runBlocking {
        val database = Database(protocol = "h2", name = "getPost")
        val repository = createRepository(database)
        val user = UsersDatabaseRepository(database).create(
            CreateUserPayload(
                "username", "displayName", "email", "password",
                LocalDate(2002, 12, 24)
            )
        ) ?: fail("Unable to create user")
        val post = repository.create(
            PostPayload("body", null, null),
            UserContext(user.id)
        ) ?: fail("Unable to create post")
        val result = repository.get(post.id, UserContext(user.id))
        assertEquals(post.id, result?.id)
        assertEquals(post.userId, result?.userId)
        assertEquals(post.repostOfId, result?.repostOfId)
        assertEquals(post.repliedToId, result?.repliedToId)
        assertEquals(post.body, result?.body)
        assertEquals(post.publishedAt, result?.publishedAt)
        assertEquals(post.editedAt, result?.editedAt)
        assertEquals(post.expiresAt, result?.expiresAt)
        assertEquals(post.visibility, result?.visibility)
        assertEquals(user.id, result?.user?.id)
        assertEquals(user.username, result?.user?.username)
        assertEquals(user.displayName, result?.user?.displayName)
    }

    @Test
    fun getPostNotExists() = runBlocking {
        val database = Database(protocol = "h2", name = "getPostNotExists")
        val repository = createRepository(database)
        val user = UsersDatabaseRepository(database).create(
            CreateUserPayload(
                "username", "displayName", "email", "password",
                LocalDate(2002, 12, 24)
            )
        ) ?: fail("Unable to create user")
        repository.create(
            PostPayload("body", null, null),
            UserContext(user.id)
        ) ?: fail("Unable to create post")
        assertEquals(null, repository.get(UUID(), UserContext(user.id)))
    }

    @Test
    fun getPostNoContext() = runBlocking {
        val database = Database(protocol = "h2", name = "getPostNoContext")
        val repository = createRepository(database)
        val user = UsersDatabaseRepository(database).create(
            CreateUserPayload(
                "username", "displayName", "email", "password",
                LocalDate(2002, 12, 24)
            )
        ) ?: fail("Unable to create user")
        val post = repository.create(
            PostPayload("body", null, null),
            UserContext(user.id)
        ) ?: fail("Unable to create post")
        assertEquals(null, repository.get(post.id))
    }

    @Test
    fun updatePost() = runBlocking {
        val database = Database(protocol = "h2", name = "updatePost")
        val repository = createRepository(database)
        val user = UsersDatabaseRepository(database).create(
            CreateUserPayload(
                "username", "displayName", "email", "password",
                LocalDate(2002, 12, 24)
            )
        ) ?: fail("Unable to create user")
        val post = repository.create(
            PostPayload("body", null, null),
            UserContext(user.id)
        ) ?: fail("Unable to create post")
        val now = Clock.System.now()
        assertEquals(
            true, repository.update(
                post.id,
                PostPayload("newBody", null, null)
            )
        )
        val postFromDatabase = database.suspendedTransaction {
            Posts
                .selectAll()
                .map {
                    Posts.toPost(it)
                }
                .singleOrNull()
        }
        assertEquals(postFromDatabase?.id, post.id)
        assertEquals(postFromDatabase?.userId, post.userId)
        assertEquals(postFromDatabase?.repostOfId, post.repostOfId)
        assertEquals(postFromDatabase?.repliedToId, post.repliedToId)
        assertEquals(postFromDatabase?.publishedAt, post.publishedAt)
        assertEquals(postFromDatabase?.expiresAt, post.expiresAt)
        assertEquals(postFromDatabase?.visibility, post.visibility)
        assertEquals(postFromDatabase?.userId, user.id)
        assertEquals(postFromDatabase?.body, "newBody")
        assertTrue(postFromDatabase?.editedAt!! >= now)
    }

    @Test
    fun updatePostNotExists() = runBlocking {
        val database = Database(protocol = "h2", name = "updatePostNotExists")
        val repository = createRepository(database)
        val user = UsersDatabaseRepository(database).create(
            CreateUserPayload(
                "username", "displayName", "email", "password",
                LocalDate(2002, 12, 24)
            )
        ) ?: fail("Unable to create user")
        repository.create(
            PostPayload("body", null, null),
            UserContext(user.id)
        ) ?: fail("Unable to create post")
        assertEquals(
            false, repository.update(
                UUID(),
                PostPayload("newBody", null, null)
            )
        )
    }

    @Test
    fun deletePost() = runBlocking {
        val database = Database(protocol = "h2", name = "deletePost")
        val repository = createRepository(database)
        val user = UsersDatabaseRepository(database).create(
            CreateUserPayload(
                "username", "displayName", "email", "password",
                LocalDate(2002, 12, 24)
            )
        ) ?: fail("Unable to create user")
        val post = repository.create(
            PostPayload("body", null, null),
            UserContext(user.id)
        ) ?: fail("Unable to create post")
        assertEquals(true, repository.delete(post.id, UserContext(user.id)))
        val count = database.suspendedTransaction {
            Posts
                .selectAll()
                .count()
        }
        assertEquals(0, count)
    }

    @Test
    fun deletePostNotExists() = runBlocking {
        val database = Database(protocol = "h2", name = "deletePostNotExists")
        val repository = createRepository(database)
        val user = UsersDatabaseRepository(database).create(
            CreateUserPayload(
                "username", "displayName", "email", "password",
                LocalDate(2002, 12, 24)
            )
        ) ?: fail("Unable to create user")
        repository.create(
            PostPayload("body", null, null),
            UserContext(user.id)
        ) ?: fail("Unable to create post")
        assertEquals(false, repository.delete(UUID(), UserContext(user.id)))
        val count = database.suspendedTransaction {
            Posts
                .selectAll()
                .count()
        }
        assertEquals(1, count)
    }

}

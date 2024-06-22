package com.extopy.controllers.posts

import com.extopy.models.posts.Post
import com.extopy.models.posts.PostPayload
import com.extopy.models.users.User
import com.extopy.models.users.UserContext
import com.extopy.usecases.posts.IGetPostRepliesUseCase
import dev.kaccelero.commons.exceptions.ControllerException
import dev.kaccelero.commons.repositories.ICreateModelWithContextSuspendUseCase
import dev.kaccelero.commons.repositories.IDeleteModelSuspendUseCase
import dev.kaccelero.commons.repositories.IGetModelWithContextSuspendUseCase
import dev.kaccelero.commons.repositories.IUpdateModelSuspendUseCase
import dev.kaccelero.commons.users.IRequireUserForCallUseCase
import dev.kaccelero.models.UUID
import dev.kaccelero.repositories.Pagination
import io.ktor.http.*
import io.ktor.server.application.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PostsControllerTest {

    private val user = User(UUID(), "displayName", "username")
    private val post = Post(UUID(), user.id, user, body = "body", publishedAt = Clock.System.now())
    private val otherPost = Post(UUID(), UUID(), user, body = "body", publishedAt = Clock.System.now())

    @Test
    fun testCreate() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val createPostUseCase = mockk<ICreateModelWithContextSuspendUseCase<Post, PostPayload>>()
        val controller = PostsController(
            requireUserForCallUseCase, createPostUseCase, mockk(), mockk(), mockk(), mockk(), mockk()
        )
        val call = mockk<ApplicationCall>()
        val payload = PostPayload("body")
        coEvery { requireUserForCallUseCase(call) } returns user
        coEvery { createPostUseCase(payload, UserContext(user.id)) } returns post
        assertEquals(post, controller.create(call, payload))
    }

    @Test
    fun testCreateInternalError() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val createPostUseCase = mockk<ICreateModelWithContextSuspendUseCase<Post, PostPayload>>()
        val controller = PostsController(
            requireUserForCallUseCase, createPostUseCase, mockk(), mockk(), mockk(), mockk(), mockk()
        )
        val call = mockk<ApplicationCall>()
        val payload = PostPayload("body")
        coEvery { requireUserForCallUseCase(call) } returns user
        coEvery { createPostUseCase(payload, UserContext(user.id)) } returns null
        val exception = assertFailsWith(ControllerException::class) {
            controller.create(call, payload)
        }
        assertEquals(HttpStatusCode.InternalServerError, exception.code)
        assertEquals("error_internal", exception.key)
    }

    @Test
    fun testGet() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getPostUseCase = mockk<IGetModelWithContextSuspendUseCase<Post, UUID>>()
        val call = mockk<ApplicationCall>()
        coEvery { requireUserForCallUseCase(call) } returns user
        coEvery { getPostUseCase(post.id, UserContext(user.id)) } returns post
        val controller = PostsController(
            requireUserForCallUseCase, mockk(), mockk(), getPostUseCase, mockk(), mockk(), mockk()
        )
        assertEquals(post, controller.get(call, post.id))
    }

    @Test
    fun testGetNotFound() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getPostUseCase = mockk<IGetModelWithContextSuspendUseCase<Post, UUID>>()
        val call = mockk<ApplicationCall>()
        coEvery { requireUserForCallUseCase(call) } returns user
        coEvery { getPostUseCase(post.id, UserContext(user.id)) } returns null
        val controller = PostsController(
            requireUserForCallUseCase, mockk(), mockk(), getPostUseCase, mockk(), mockk(), mockk()
        )
        val exception = assertFailsWith(ControllerException::class) {
            controller.get(call, post.id)
        }
        assertEquals(HttpStatusCode.NotFound, exception.code)
        assertEquals("posts_not_found", exception.key)
    }

    @Test
    fun testUpdate() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getPostUseCase = mockk<IGetModelWithContextSuspendUseCase<Post, UUID>>()
        val updatePostUseCase = mockk<IUpdateModelSuspendUseCase<Post, UUID, PostPayload>>()
        val call = mockk<ApplicationCall>()
        val updatedPost = post.copy(
            body = "newBody"
        )
        val payload = PostPayload("newBody")
        coEvery { requireUserForCallUseCase(call) } returns user
        coEvery { getPostUseCase(post.id, UserContext(user.id)) } returns post
        coEvery { updatePostUseCase(post.id, payload) } returns updatedPost
        val controller = PostsController(
            requireUserForCallUseCase, mockk(), mockk(), getPostUseCase, updatePostUseCase, mockk(), mockk()
        )
        assertEquals(updatedPost, controller.update(call, post.id, payload))
    }

    @Test
    fun testUpdateNotFound() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getPostUseCase = mockk<IGetModelWithContextSuspendUseCase<Post, UUID>>()
        val call = mockk<ApplicationCall>()
        val payload = PostPayload("newBody")
        coEvery { requireUserForCallUseCase(call) } returns user
        coEvery { getPostUseCase(post.id, UserContext(user.id)) } returns null
        val controller = PostsController(
            requireUserForCallUseCase, mockk(), mockk(), getPostUseCase, mockk(), mockk(), mockk()
        )
        val exception = assertFailsWith(ControllerException::class) {
            controller.update(call, post.id, payload)
        }
        assertEquals(HttpStatusCode.NotFound, exception.code)
        assertEquals("posts_not_found", exception.key)
    }

    @Test
    fun testUpdateForbidden() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getPostUseCase = mockk<IGetModelWithContextSuspendUseCase<Post, UUID>>()
        val call = mockk<ApplicationCall>()
        val payload = PostPayload("newBody")
        coEvery { requireUserForCallUseCase(call) } returns user
        coEvery { getPostUseCase(otherPost.id, UserContext(user.id)) } returns otherPost
        val controller = PostsController(
            requireUserForCallUseCase, mockk(), mockk(), getPostUseCase, mockk(), mockk(), mockk()
        )
        val exception = assertFailsWith(ControllerException::class) {
            controller.update(call, otherPost.id, payload)
        }
        assertEquals(HttpStatusCode.Forbidden, exception.code)
        assertEquals("posts_update_not_allowed", exception.key)
    }

    @Test
    fun testUpdateInternalError() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getPostUseCase = mockk<IGetModelWithContextSuspendUseCase<Post, UUID>>()
        val updatePostUseCase = mockk<IUpdateModelSuspendUseCase<Post, UUID, PostPayload>>()
        val call = mockk<ApplicationCall>()
        val payload = PostPayload("newBody")
        coEvery { requireUserForCallUseCase(call) } returns user
        coEvery { getPostUseCase(post.id, UserContext(user.id)) } returns post
        coEvery { updatePostUseCase(post.id, payload) } returns null
        val controller = PostsController(
            requireUserForCallUseCase, mockk(), mockk(), getPostUseCase, updatePostUseCase, mockk(), mockk()
        )
        val exception = assertFailsWith(ControllerException::class) {
            controller.update(call, post.id, payload)
        }
        assertEquals(HttpStatusCode.InternalServerError, exception.code)
        assertEquals("error_internal", exception.key)
    }

    @Test
    fun testDelete() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getPostUseCase = mockk<IGetModelWithContextSuspendUseCase<Post, UUID>>()
        val deletePostUseCase = mockk<IDeleteModelSuspendUseCase<Post, UUID>>()
        val call = mockk<ApplicationCall>()
        coEvery { requireUserForCallUseCase(call) } returns user
        coEvery { getPostUseCase(post.id, UserContext(user.id)) } returns post
        coEvery { deletePostUseCase(post.id) } returns true
        val controller = PostsController(
            requireUserForCallUseCase, mockk(), mockk(), getPostUseCase, mockk(), deletePostUseCase, mockk()
        )
        controller.delete(call, post.id)
        coVerify { deletePostUseCase(post.id) }
    }

    @Test
    fun testDeleteNotFound() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getPostUseCase = mockk<IGetModelWithContextSuspendUseCase<Post, UUID>>()
        val call = mockk<ApplicationCall>()
        coEvery { requireUserForCallUseCase(call) } returns user
        coEvery { getPostUseCase(post.id, UserContext(user.id)) } returns null
        val controller = PostsController(
            requireUserForCallUseCase, mockk(), mockk(), getPostUseCase, mockk(), mockk(), mockk()
        )
        val exception = assertFailsWith(ControllerException::class) {
            controller.delete(call, post.id)
        }
        assertEquals(HttpStatusCode.NotFound, exception.code)
        assertEquals("posts_not_found", exception.key)
    }

    @Test
    fun testDeleteForbidden() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getPostUseCase = mockk<IGetModelWithContextSuspendUseCase<Post, UUID>>()
        val call = mockk<ApplicationCall>()
        coEvery { requireUserForCallUseCase(call) } returns user
        coEvery { getPostUseCase(otherPost.id, UserContext(user.id)) } returns otherPost
        val controller = PostsController(
            requireUserForCallUseCase, mockk(), mockk(), getPostUseCase, mockk(), mockk(), mockk()
        )
        val exception = assertFailsWith(ControllerException::class) {
            controller.delete(call, otherPost.id)
        }
        assertEquals(HttpStatusCode.Forbidden, exception.code)
        assertEquals("posts_delete_not_allowed", exception.key)
    }

    @Test
    fun testDeleteInternalError() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getPostUseCase = mockk<IGetModelWithContextSuspendUseCase<Post, UUID>>()
        val deletePostUseCase = mockk<IDeleteModelSuspendUseCase<Post, UUID>>()
        val call = mockk<ApplicationCall>()
        coEvery { requireUserForCallUseCase(call) } returns user
        coEvery { getPostUseCase(post.id, UserContext(user.id)) } returns post
        coEvery { deletePostUseCase(post.id) } returns false
        val controller = PostsController(
            requireUserForCallUseCase, mockk(), mockk(), getPostUseCase, mockk(), deletePostUseCase, mockk()
        )
        val exception = assertFailsWith(ControllerException::class) {
            controller.delete(call, post.id)
        }
        assertEquals(HttpStatusCode.InternalServerError, exception.code)
        assertEquals("error_internal", exception.key)
    }

    @Test
    fun testGetReplies() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getPostUseCase = mockk<IGetModelWithContextSuspendUseCase<Post, UUID>>()
        val getPostRepliesUseCase = mockk<IGetPostRepliesUseCase>()
        coEvery { requireUserForCallUseCase(any()) } returns user
        coEvery { getPostUseCase(post.id, UserContext(user.id)) } returns post
        coEvery { getPostRepliesUseCase(post.id, Pagination(10, 5), UserContext(user.id)) } returns listOf(post)
        val controller = PostsController(
            requireUserForCallUseCase, mockk(), mockk(), getPostUseCase, mockk(), mockk(), getPostRepliesUseCase
        )
        assertEquals(listOf(post), controller.listReplies(mockk(), post.id, 10, 5))
    }

    @Test
    fun testGetRepliesNotFound() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getPostUseCase = mockk<IGetModelWithContextSuspendUseCase<Post, UUID>>()
        coEvery { requireUserForCallUseCase(any()) } returns user
        coEvery { getPostUseCase(post.id, UserContext(user.id)) } returns null
        val controller = PostsController(
            requireUserForCallUseCase, mockk(), mockk(), getPostUseCase, mockk(), mockk(), mockk()
        )
        val exception = assertFailsWith(ControllerException::class) {
            controller.listReplies(mockk(), post.id, null, null)
        }
        assertEquals(HttpStatusCode.NotFound, exception.code)
        assertEquals("posts_not_found", exception.key)
    }

    @Test
    fun testGetRepliesDefaultLimitOffset() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getPostUseCase = mockk<IGetModelWithContextSuspendUseCase<Post, UUID>>()
        val getPostRepliesUseCase = mockk<IGetPostRepliesUseCase>()
        coEvery { requireUserForCallUseCase(any()) } returns user
        coEvery { getPostUseCase(post.id, UserContext(user.id)) } returns post
        coEvery { getPostRepliesUseCase(post.id, Pagination(25, 0), UserContext(user.id)) } returns listOf(post)
        val controller = PostsController(
            requireUserForCallUseCase, mockk(), mockk(), getPostUseCase, mockk(), mockk(), getPostRepliesUseCase
        )
        assertEquals(listOf(post), controller.listReplies(mockk(), post.id, null, null))
    }

}

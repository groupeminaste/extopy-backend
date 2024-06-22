package com.extopy.controllers.users

import com.extopy.models.posts.Post
import com.extopy.models.users.UpdateUserPayload
import com.extopy.models.users.User
import com.extopy.models.users.UserContext
import com.extopy.usecases.users.IGetUserPostsUseCase
import dev.kaccelero.commons.exceptions.ControllerException
import dev.kaccelero.commons.repositories.IGetModelWithContextSuspendUseCase
import dev.kaccelero.commons.repositories.IUpdateModelSuspendUseCase
import dev.kaccelero.commons.users.IRequireUserForCallUseCase
import dev.kaccelero.models.UUID
import dev.kaccelero.repositories.Pagination
import io.ktor.http.*
import io.ktor.server.application.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UsersControllerTest {

    private val user = User(UUID(), "displayName", "username")
    private val targetUser = User(UUID(), "targetDisplayName", "targetUsername")

    @Test
    fun testGet() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getUserUseCase = mockk<IGetModelWithContextSuspendUseCase<User, UUID>>()
        val call = mockk<ApplicationCall>()
        coEvery { requireUserForCallUseCase(call) } returns user
        coEvery { getUserUseCase(targetUser.id, UserContext(user.id)) } returns targetUser
        val controller = UsersController(
            requireUserForCallUseCase,
            mockk(),
            getUserUseCase,
            mockk(),
            mockk()
        )
        assertEquals(targetUser, controller.get(call, targetUser.id))
    }

    @Test
    fun testGetNotFound() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getUserUseCase = mockk<IGetModelWithContextSuspendUseCase<User, UUID>>()
        val call = mockk<ApplicationCall>()
        coEvery { requireUserForCallUseCase(call) } returns user
        coEvery { getUserUseCase(targetUser.id, UserContext(user.id)) } returns null
        val controller = UsersController(
            requireUserForCallUseCase,
            mockk(),
            getUserUseCase,
            mockk(),
            mockk()
        )
        val exception = assertFailsWith(ControllerException::class) {
            controller.get(call, targetUser.id)
        }
        assertEquals(HttpStatusCode.NotFound, exception.code)
        assertEquals("users_not_found", exception.key)
    }

    @Test
    fun testUpdate() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val updateUserUseCase = mockk<IUpdateModelSuspendUseCase<User, UUID, UpdateUserPayload>>()
        val call = mockk<ApplicationCall>()
        val updatedUser = user.copy(
            username = "newUsername",
            displayName = "new display name"
        )
        val payload = UpdateUserPayload(
            "newUsername", "new display name"
        )
        coEvery { requireUserForCallUseCase(call) } returns user
        coEvery { updateUserUseCase(user.id, payload) } returns updatedUser
        val controller = UsersController(
            requireUserForCallUseCase,
            mockk(),
            mockk(),
            updateUserUseCase,
            mockk()
        )
        assertEquals(updatedUser, controller.update(call, user.id, payload))
    }

    @Test
    fun testUpdateForbidden() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val call = mockk<ApplicationCall>()
        coEvery { requireUserForCallUseCase(call) } returns user
        val controller = UsersController(
            requireUserForCallUseCase,
            mockk(),
            mockk(),
            mockk(),
            mockk()
        )
        val exception = assertFailsWith(ControllerException::class) {
            controller.update(call, targetUser.id, UpdateUserPayload())
        }
        assertEquals(HttpStatusCode.Forbidden, exception.code)
        assertEquals("users_update_not_allowed", exception.key)
    }

    @Test
    fun testUpdateInternalError() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val updateUserUseCase = mockk<IUpdateModelSuspendUseCase<User, UUID, UpdateUserPayload>>()
        val call = mockk<ApplicationCall>()
        val payload = UpdateUserPayload(
            "newUsername", "new display name"
        )
        coEvery { requireUserForCallUseCase(call) } returns user
        coEvery { updateUserUseCase(user.id, payload) } returns null
        val controller = UsersController(
            requireUserForCallUseCase,
            mockk(),
            mockk(),
            updateUserUseCase,
            mockk()
        )
        val exception = assertFailsWith(ControllerException::class) {
            controller.update(call, user.id, payload)
        }
        assertEquals(HttpStatusCode.InternalServerError, exception.code)
        assertEquals("error_internal", exception.key)
    }

    @Test
    fun testGetPosts() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getUserUseCase = mockk<IGetModelWithContextSuspendUseCase<User, UUID>>()
        val getUserPostsUseCase = mockk<IGetUserPostsUseCase>()
        val post = Post(UUID(), targetUser.id, user, body = "body", publishedAt = Clock.System.now())
        coEvery { requireUserForCallUseCase(any()) } returns user
        coEvery { getUserUseCase(targetUser.id, UserContext(user.id)) } returns targetUser
        coEvery { getUserPostsUseCase(targetUser.id, Pagination(10, 5), UserContext(user.id)) } returns listOf(post)
        val controller = UsersController(
            requireUserForCallUseCase,
            mockk(),
            getUserUseCase,
            mockk(),
            getUserPostsUseCase
        )
        assertEquals(listOf(post), controller.listPosts(mockk(), targetUser.id, 10, 5))
    }

    @Test
    fun testGetPostsNotFound() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getUserUseCase = mockk<IGetModelWithContextSuspendUseCase<User, UUID>>()
        coEvery { requireUserForCallUseCase(any()) } returns user
        coEvery { getUserUseCase(targetUser.id, UserContext(user.id)) } returns null
        val controller = UsersController(
            requireUserForCallUseCase,
            mockk(),
            getUserUseCase,
            mockk(),
            mockk()
        )
        val exception = assertFailsWith(ControllerException::class) {
            controller.listPosts(mockk(), targetUser.id, null, null)
        }
        assertEquals(HttpStatusCode.NotFound, exception.code)
        assertEquals("users_not_found", exception.key)
    }

    @Test
    fun testGetPostsDefaultLimitOffset() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getUserUseCase = mockk<IGetModelWithContextSuspendUseCase<User, UUID>>()
        val getUserPostsUseCase = mockk<IGetUserPostsUseCase>()
        val post = Post(UUID(), targetUser.id, user, body = "body", publishedAt = Clock.System.now())
        coEvery { requireUserForCallUseCase(any()) } returns user
        coEvery { getUserUseCase(targetUser.id, UserContext(user.id)) } returns targetUser
        coEvery { getUserPostsUseCase(targetUser.id, Pagination(25, 0), UserContext(user.id)) } returns listOf(post)
        val controller = UsersController(
            requireUserForCallUseCase,
            mockk(),
            getUserUseCase,
            mockk(),
            getUserPostsUseCase
        )
        assertEquals(listOf(post), controller.listPosts(mockk(), targetUser.id, null, null))
    }

}

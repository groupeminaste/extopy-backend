package me.nathanfallet.extopy.controllers.users

import io.ktor.http.*
import io.ktor.server.application.*
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.users.CreateUserPayload
import me.nathanfallet.extopy.models.users.UpdateUserPayload
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.extopy.usecases.users.IGetUserPostsUseCase
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.ktorx.usecases.users.IRequireUserForCallUseCase
import me.nathanfallet.usecases.models.get.context.IGetModelWithContextSuspendUseCase
import me.nathanfallet.usecases.models.update.IUpdateModelSuspendUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UsersControllerTest {

    private val user = User("id", "displayName", "username")
    private val targetUser = User("targetId", "targetDisplayName", "targetUsername")

    @Test
    fun testList() = runBlocking {
        val controller = UsersController(mockk(), mockk(), mockk(), mockk())
        val exception = assertFailsWith(ControllerException::class) {
            controller.list(mockk())
        }
        assertEquals(HttpStatusCode.MethodNotAllowed, exception.code)
        assertEquals("users_list_not_allowed", exception.key)
    }

    @Test
    fun testCreate() = runBlocking {
        val controller = UsersController(mockk(), mockk(), mockk(), mockk())
        val exception = assertFailsWith(ControllerException::class) {
            controller.create(
                mockk(),
                CreateUserPayload(
                    "username", "displayName", "email", "password",
                    LocalDate(2002, 12, 24)
                )
            )
        }
        assertEquals(HttpStatusCode.MethodNotAllowed, exception.code)
        assertEquals("users_create_not_allowed", exception.key)
    }

    @Test
    fun testDelete() = runBlocking {
        val controller = UsersController(mockk(), mockk(), mockk(), mockk())
        val exception = assertFailsWith(ControllerException::class) {
            controller.delete(mockk(), "userId")
        }
        assertEquals(HttpStatusCode.MethodNotAllowed, exception.code)
        assertEquals("users_delete_not_allowed", exception.key)
    }

    @Test
    fun testGet() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getUserUseCase = mockk<IGetModelWithContextSuspendUseCase<User, String>>()
        val call = mockk<ApplicationCall>()
        coEvery { requireUserForCallUseCase(call) } returns user
        coEvery { getUserUseCase(targetUser.id, UserContext(user.id)) } returns targetUser
        val controller = UsersController(
            requireUserForCallUseCase,
            getUserUseCase,
            mockk(),
            mockk()
        )
        assertEquals(targetUser, controller.get(call, targetUser.id))
    }

    @Test
    fun testGetNotFound() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getUserUseCase = mockk<IGetModelWithContextSuspendUseCase<User, String>>()
        val call = mockk<ApplicationCall>()
        coEvery { requireUserForCallUseCase(call) } returns user
        coEvery { getUserUseCase(targetUser.id, UserContext(user.id)) } returns null
        val controller = UsersController(
            requireUserForCallUseCase,
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
        val updateUserUseCase = mockk<IUpdateModelSuspendUseCase<User, String, UpdateUserPayload>>()
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
        val updateUserUseCase = mockk<IUpdateModelSuspendUseCase<User, String, UpdateUserPayload>>()
        val call = mockk<ApplicationCall>()
        val payload = UpdateUserPayload(
            "newUsername", "new display name"
        )
        coEvery { requireUserForCallUseCase(call) } returns user
        coEvery { updateUserUseCase(user.id, payload) } returns null
        val controller = UsersController(
            requireUserForCallUseCase,
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
        val getUserPostsUseCase = mockk<IGetUserPostsUseCase>()
        val call = mockk<ApplicationCall>()
        val post = Post("postId", "userId", user, body = "body")
        coEvery { requireUserForCallUseCase(call) } returns user
        coEvery { getUserPostsUseCase(targetUser.id, 10, 5, UserContext(user.id)) } returns listOf(post)
        every { call.parameters["limit"] } returns "10"
        every { call.parameters["offset"] } returns "5"
        val controller = UsersController(
            requireUserForCallUseCase,
            mockk(),
            mockk(),
            getUserPostsUseCase
        )
        assertEquals(listOf(post), controller.getPosts(call, targetUser.id))
    }

    @Test
    fun testGetPostsDefaultLimitOffset() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getUserPostsUseCase = mockk<IGetUserPostsUseCase>()
        val call = mockk<ApplicationCall>()
        val post = Post("postId", "userId", user, body = "body")
        coEvery { requireUserForCallUseCase(call) } returns user
        coEvery { getUserPostsUseCase(targetUser.id, 25, 0, UserContext(user.id)) } returns listOf(post)
        every { call.parameters["limit"] } returns null
        every { call.parameters["offset"] } returns null
        val controller = UsersController(
            requireUserForCallUseCase,
            mockk(),
            mockk(),
            getUserPostsUseCase
        )
        assertEquals(listOf(post), controller.getPosts(call, targetUser.id))
    }

    @Test
    fun testGetPostsInvalidLimitOffset() = runBlocking {
        val requireUserForCallUseCase = mockk<IRequireUserForCallUseCase>()
        val getUserPostsUseCase = mockk<IGetUserPostsUseCase>()
        val call = mockk<ApplicationCall>()
        val post = Post("postId", "userId", user, body = "body")
        coEvery { requireUserForCallUseCase(call) } returns user
        coEvery { getUserPostsUseCase(targetUser.id, 25, 0, UserContext(user.id)) } returns listOf(post)
        every { call.parameters["limit"] } returns "a"
        every { call.parameters["offset"] } returns "b"
        val controller = UsersController(
            requireUserForCallUseCase,
            mockk(),
            mockk(),
            getUserPostsUseCase
        )
        assertEquals(listOf(post), controller.getPosts(call, targetUser.id))
    }

}

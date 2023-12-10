package me.nathanfallet.extopy.usecases.posts

import io.ktor.http.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.posts.PostPayload
import me.nathanfallet.extopy.repositories.posts.IPostsRepository
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UpdatePostUseCaseTest {

    @Test
    fun testInvoke() = runBlocking {
        val repository = mockk<IPostsRepository>()
        val useCase = UpdatePostUseCase(repository)
        val payload = PostPayload("body")
        val post = mockk<Post>()
        coEvery { repository.update("id", payload) } returns true
        coEvery { repository.get("id") } returns post
        assertEquals(post, useCase("id", payload))
    }

    @Test
    fun testInvokeFails() = runBlocking {
        val repository = mockk<IPostsRepository>()
        val useCase = UpdatePostUseCase(repository)
        val payload = PostPayload("body")
        coEvery { repository.update("id", payload) } returns false
        assertEquals(null, useCase("id", payload))
    }

    @Test
    fun testInvokeNoBody() = runBlocking {
        val useCase = UpdatePostUseCase(mockk())
        val payload = PostPayload("")
        val exception = assertFailsWith<ControllerException> {
            useCase("id", payload)
        }
        assertEquals(HttpStatusCode.BadRequest, exception.code)
        assertEquals("posts_body_empty", exception.key)
    }

}

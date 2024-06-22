package com.extopy.usecases.posts

import com.extopy.models.posts.Post
import com.extopy.models.posts.PostPayload
import com.extopy.repositories.posts.IPostsRepository
import dev.kaccelero.commons.exceptions.ControllerException
import dev.kaccelero.models.UUID
import io.ktor.http.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UpdatePostUseCaseTest {

    @Test
    fun testInvoke() = runBlocking {
        val repository = mockk<IPostsRepository>()
        val useCase = UpdatePostUseCase(repository)
        val payload = PostPayload("body")
        val postId = UUID()
        val post = mockk<Post>()
        coEvery { repository.update(postId, payload) } returns true
        coEvery { repository.get(postId) } returns post
        assertEquals(post, useCase(postId, payload))
    }

    @Test
    fun testInvokeFails() = runBlocking {
        val repository = mockk<IPostsRepository>()
        val useCase = UpdatePostUseCase(repository)
        val payload = PostPayload("body")
        val postId = UUID()
        coEvery { repository.update(postId, payload) } returns false
        assertEquals(null, useCase(postId, payload))
    }

    @Test
    fun testInvokeNoBody() = runBlocking {
        val useCase = UpdatePostUseCase(mockk())
        val payload = PostPayload("")
        val exception = assertFailsWith<ControllerException> {
            useCase(UUID(), payload)
        }
        assertEquals(HttpStatusCode.BadRequest, exception.code)
        assertEquals("posts_body_empty", exception.key)
    }

}

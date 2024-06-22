package com.extopy.usecases.posts

import com.extopy.repositories.posts.IPostsRepository
import dev.kaccelero.models.UUID
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class DeletePostUseCaseTest {

    @Test
    fun testInvoke() = runBlocking {
        val repository = mockk<IPostsRepository>()
        val useCase = DeletePostUseCase(repository)
        val id = UUID()
        coEvery { repository.delete(id) } returns true
        assertEquals(true, useCase(id))
    }

    @Test
    fun testInvokeFails() = runBlocking {
        val repository = mockk<IPostsRepository>()
        val useCase = DeletePostUseCase(repository)
        val id = UUID()
        coEvery { repository.delete(id) } returns false
        assertEquals(false, useCase(id))
    }

}

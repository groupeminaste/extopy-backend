package com.extopy.usecases.posts

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import com.extopy.repositories.posts.IPostsRepository
import kotlin.test.Test
import kotlin.test.assertEquals

class DeletePostUseCaseTest {

    @Test
    fun testInvoke() = runBlocking {
        val repository = mockk<IPostsRepository>()
        val useCase = DeletePostUseCase(repository)
        coEvery { repository.delete("id") } returns true
        assertEquals(true, useCase("id"))
    }

    @Test
    fun testInvokeFails() = runBlocking {
        val repository = mockk<IPostsRepository>()
        val useCase = DeletePostUseCase(repository)
        coEvery { repository.delete("id") } returns false
        assertEquals(false, useCase("id"))
    }

}

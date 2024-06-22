package com.extopy.usecases.users

import com.extopy.models.posts.Post
import com.extopy.models.users.UserContext
import com.extopy.repositories.posts.IPostsRepository
import dev.kaccelero.models.UUID
import dev.kaccelero.repositories.Pagination
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class GetUserPostsUseCaseTest {

    @Test
    fun testInvoke() = runBlocking {
        val postsRepository = mockk<IPostsRepository>()
        val useCase = GetUserPostsUseCase(postsRepository)
        val posts = mockk<List<Post>>()
        val userId = UUID()
        val otherUserId = UUID()
        coEvery { postsRepository.listUserPosts(userId, Pagination(25, 0), UserContext(otherUserId)) } returns posts
        assertEquals(posts, useCase(userId, Pagination(25, 0), UserContext(otherUserId)))
    }

}

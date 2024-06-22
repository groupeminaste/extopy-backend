package com.extopy.usecases.posts

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

class GetPostRepliesUseCaseTest {

    @Test
    fun testInvoke() = runBlocking {
        val postsRepository = mockk<IPostsRepository>()
        val useCase = GetPostRepliesUseCase(postsRepository)
        val postId = UUID()
        val userId = UUID()
        val posts = mockk<List<Post>>()
        coEvery { postsRepository.listReplies(postId, Pagination(25, 0), UserContext(userId)) } returns posts
        assertEquals(posts, useCase(postId, Pagination(25, 0), UserContext(userId)))
    }

}

package com.extopy.usecases.posts

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import com.extopy.models.posts.Post
import com.extopy.models.users.UserContext
import com.extopy.repositories.posts.IPostsRepository
import dev.kaccelero.repositories.Pagination
import kotlin.test.Test
import kotlin.test.assertEquals

class GetPostRepliesUseCaseTest {

    @Test
    fun testInvoke() = runBlocking {
        val postsRepository = mockk<IPostsRepository>()
        val useCase = GetPostRepliesUseCase(postsRepository)
        val posts = mockk<List<Post>>()
        coEvery { postsRepository.listReplies("postId", Pagination(25, 0), UserContext("userId")) } returns posts
        assertEquals(posts, useCase("postId", Pagination(25, 0), UserContext("userId")))
    }

}

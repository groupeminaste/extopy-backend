package me.nathanfallet.extopy.usecases.posts

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.extopy.repositories.posts.IPostsRepository
import kotlin.test.Test
import kotlin.test.assertEquals

class GetPostRepliesUseCaseTest {

    @Test
    fun testInvoke() = runBlocking {
        val postsRepository = mockk<IPostsRepository>()
        val useCase = GetPostRepliesUseCase(postsRepository)
        val posts = mockk<List<Post>>()
        coEvery { postsRepository.listReplies("postId", 25, 0, UserContext("userId")) } returns posts
        assertEquals(posts, useCase("postId", 25, 0, UserContext("userId")))
    }

}

package me.nathanfallet.extopy.usecases.posts

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.extopy.repositories.posts.IPostsRepository
import me.nathanfallet.usecases.pagination.Pagination
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

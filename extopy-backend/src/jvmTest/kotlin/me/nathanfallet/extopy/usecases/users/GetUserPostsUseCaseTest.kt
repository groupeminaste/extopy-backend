package me.nathanfallet.extopy.usecases.users

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.extopy.repositories.posts.IPostsRepository
import me.nathanfallet.usecases.pagination.Pagination
import kotlin.test.Test
import kotlin.test.assertEquals

class GetUserPostsUseCaseTest {

    @Test
    fun testInvoke() = runBlocking {
        val postsRepository = mockk<IPostsRepository>()
        val useCase = GetUserPostsUseCase(postsRepository)
        val posts = mockk<List<Post>>()
        coEvery { postsRepository.listUserPosts("userId", Pagination(25, 0), UserContext("otherUserId")) } returns posts
        assertEquals(posts, useCase("userId", Pagination(25, 0), UserContext("otherUserId")))
    }

}

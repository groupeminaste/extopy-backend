package me.nathanfallet.extopy.usecases.timelines

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.extopy.repositories.posts.IPostsRepository
import me.nathanfallet.usecases.pagination.Pagination
import kotlin.test.Test
import kotlin.test.assertEquals

class GetTimelinePostsUseCaseTest {

    @Test
    fun testGetDefaultTimeline() = runBlocking {
        val postsRepository = mockk<IPostsRepository>()
        val useCase = GetTimelinePostsUseCase(postsRepository)
        coEvery { postsRepository.listDefault(Pagination(25, 0), UserContext("userId")) } returns listOf(
            Post("postId")
        )
        assertEquals(
            listOf(Post("postId")),
            useCase("default", Pagination(25, 0), UserContext("userId"))
        )
    }

    @Test
    fun testGetTrendsTimeline() = runBlocking {
        val postsRepository = mockk<IPostsRepository>()
        val useCase = GetTimelinePostsUseCase(postsRepository)
        coEvery { postsRepository.listTrends(Pagination(25, 0), UserContext("userId")) } returns listOf(
            Post("postId")
        )
        assertEquals(
            listOf(Post("postId")),
            useCase("trends", Pagination(25, 0), UserContext("userId"))
        )
    }

}

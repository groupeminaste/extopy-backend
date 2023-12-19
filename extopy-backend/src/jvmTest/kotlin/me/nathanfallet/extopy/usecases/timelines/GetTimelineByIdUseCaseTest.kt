package me.nathanfallet.extopy.usecases.timelines

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.timelines.Timeline
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.extopy.repositories.posts.IPostsRepository
import kotlin.test.Test
import kotlin.test.assertEquals

class GetTimelineByIdUseCaseTest {

    @Test
    fun testGetDefaultTimeline() = runBlocking {
        val postsRepository = mockk<IPostsRepository>()
        val useCase = GetTimelineByIdUseCase(mockk(), postsRepository)
        coEvery { postsRepository.list(25, 0, UserContext("userId")) } returns listOf(
            Post("postId")
        )
        assertEquals(
            Timeline(
                "default",
                "default",
                posts = listOf(
                    Post("postId")
                )
            ),
            useCase("default", UserContext("userId"), 25, 0)
        )
    }

    @Test
    fun testGetUnknownTypeTimeline() = runBlocking {
        val useCase = GetTimelineByIdUseCase(mockk(), mockk())
        assertEquals(
            null,
            useCase("unknown", UserContext("userId"), 25, 0)
        )
    }

}

package com.extopy.usecases.timelines

import com.extopy.models.posts.Post
import com.extopy.models.timelines.Timeline
import com.extopy.models.users.UserContext
import com.extopy.repositories.posts.IPostsRepository
import dev.kaccelero.models.UUID
import dev.kaccelero.repositories.Pagination
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals

class GetTimelinePostsUseCaseTest {

    @Test
    fun testGetDefaultTimeline() = runBlocking {
        val postsRepository = mockk<IPostsRepository>()
        val useCase = GetTimelinePostsUseCase(postsRepository)
        val userId = UUID()
        val postId = UUID()
        val publishedAt = Clock.System.now()
        coEvery { postsRepository.listDefault(Pagination(25, 0), UserContext(userId)) } returns listOf(
            Post(postId, userId, publishedAt = publishedAt)
        )
        assertEquals(
            listOf(Post(postId, userId, publishedAt = publishedAt)),
            useCase(Timeline.defaultId, Pagination(25, 0), UserContext(userId))
        )
    }

}

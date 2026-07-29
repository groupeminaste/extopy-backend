package com.extopy.domain.usecases.timelines

import com.extopy.domain.models.application.Pagination
import com.extopy.domain.models.posts.Post
import com.extopy.domain.models.timelines.Timeline
import com.extopy.domain.repositories.PostsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.Instant
import kotlin.uuid.Uuid

class TimelineUseCasesTest {

    private val userId = Uuid.parse("00000000-0000-4000-8000-000000000001")

    private val post = Post(
        id = Uuid.parse("00000000-0000-4000-8000-000000000002"),
        userId = userId,
        body = "Hello world!",
        publishedAt = Instant.parse("2026-01-02T03:04:05Z"),
    )

    @Test
    fun testOnlyTheDefaultTimelineExists() = runTest {
        assertNotNull(GetTimelineUseCaseImpl()(Timeline.DEFAULT_ID))
        assertNull(GetTimelineUseCaseImpl()(userId))
    }

    @Test
    fun testTheDefaultTimelineIsWhatYouFollow() = runTest {
        val repository = mockk<PostsRepository>()
        coEvery { repository.listDefault(any(), userId) } returns listOf(post)

        val result = GetTimelinePostsUseCaseImpl(repository)(Timeline.DEFAULT_ID, Pagination(), userId)

        assertEquals(listOf(post), result)
    }

    /**
     * An account that follows nobody yet would otherwise land on an empty page, so it gets the
     * trends instead until it has something of its own to read.
     */
    @Test
    fun testAnEmptyTimelineFallsBackToTheTrends() = runTest {
        val repository = mockk<PostsRepository>()
        coEvery { repository.listDefault(any(), userId) } returns emptyList()
        coEvery { repository.listTrends(any(), userId) } returns listOf(post)

        val result = GetTimelinePostsUseCaseImpl(repository)(Timeline.DEFAULT_ID, Pagination(), userId)

        assertEquals(listOf(post), result)
        coVerify { repository.listTrends(any(), userId) }
    }

    @Test
    fun testACustomTimelineHasNothingInItYet() = runTest {
        val repository = mockk<PostsRepository>()

        val result = GetTimelinePostsUseCaseImpl(repository)(userId, Pagination(), userId)

        assertEquals(emptyList(), result)
    }

}

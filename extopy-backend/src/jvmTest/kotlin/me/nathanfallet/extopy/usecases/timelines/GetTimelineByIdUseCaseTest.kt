package me.nathanfallet.extopy.usecases.timelines

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.timelines.Timeline
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.extopy.repositories.posts.IPostsRepository
import me.nathanfallet.extopy.repositories.users.IUsersRepository
import kotlin.test.Test
import kotlin.test.assertEquals

class GetTimelineByIdUseCaseTest {

    @Test
    fun testGetUnknownTypeTimeline() = runBlocking {
        val useCase = GetTimelineByIdUseCase(mockk(), mockk())
        assertEquals(
            null,
            useCase("unknown", UserContext("userId"), 25, 0)
        )
    }

    @Test
    fun testGetDefaultTimeline() = runBlocking {
        val postsRepository = mockk<IPostsRepository>()
        val useCase = GetTimelineByIdUseCase(mockk(), postsRepository)
        coEvery { postsRepository.listDefault(25, 0, UserContext("userId")) } returns listOf(
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
    fun testGetTrendsTimeline() = runBlocking {
        val postsRepository = mockk<IPostsRepository>()
        val useCase = GetTimelineByIdUseCase(mockk(), postsRepository)
        coEvery { postsRepository.listTrends(25, 0, UserContext("userId")) } returns listOf(
            Post("postId")
        )
        assertEquals(
            Timeline(
                "trends",
                "trends",
                posts = listOf(
                    Post("postId")
                )
            ),
            useCase("trends", UserContext("userId"), 25, 0)
        )
    }

    @Test
    fun testGetUsersTimeline() = runBlocking {
        val usersRepository = mockk<IUsersRepository>()
        val postsRepository = mockk<IPostsRepository>()
        val useCase = GetTimelineByIdUseCase(usersRepository, postsRepository)
        coEvery { usersRepository.get("otherUserId", UserContext("userId")) } returns User(
            "otherUserId",
            "User",
            "user"
        )
        coEvery { postsRepository.listUserPosts("otherUserId", 25, 0, UserContext("userId")) } returns listOf(
            Post("postId")
        )
        assertEquals(
            Timeline(
                "otherUserId",
                "users",
                users = listOf(
                    User("otherUserId", "User", "user")
                ),
                posts = listOf(
                    Post("postId")
                )
            ),
            useCase("users:otherUserId", UserContext("userId"), 25, 0)
        )
    }

    @Test
    fun testGetUsersTimelineNotFound() = runBlocking {
        val usersRepository = mockk<IUsersRepository>()
        val useCase = GetTimelineByIdUseCase(usersRepository, mockk())
        coEvery { usersRepository.get("otherUserId", UserContext("userId")) } returns null
        assertEquals(
            null,
            useCase("users:otherUserId", UserContext("userId"), 25, 0)
        )
    }

    @Test
    fun testGetUsersTimelineNoId() = runBlocking {
        val useCase = GetTimelineByIdUseCase(mockk(), mockk())
        assertEquals(
            null,
            useCase("users", UserContext("userId"), 25, 0)
        )
    }

    @Test
    fun testGetPostsTimeline() = runBlocking {
        val postsRepository = mockk<IPostsRepository>()
        val useCase = GetTimelineByIdUseCase(mockk(), postsRepository)
        coEvery { postsRepository.get("postId", UserContext("userId")) } returns Post("postId")
        coEvery { postsRepository.listReplies("postId", 25, 0, UserContext("userId")) } returns listOf(
            Post("replyPostId")
        )
        assertEquals(
            Timeline(
                "postId",
                "posts",
                posts = listOf(
                    Post("postId"), Post("replyPostId")
                )
            ),
            useCase("posts:postId", UserContext("userId"), 25, 0)
        )
    }

    @Test
    fun testGetPostsTimelineNotFound() = runBlocking {
        val postsRepository = mockk<IPostsRepository>()
        val useCase = GetTimelineByIdUseCase(mockk(), postsRepository)
        coEvery { postsRepository.get("postId", UserContext("userId")) } returns null
        assertEquals(
            null,
            useCase("posts:postId", UserContext("userId"), 25, 0)
        )
    }

    @Test
    fun testGetPostsTimelineNoId() = runBlocking {
        val useCase = GetTimelineByIdUseCase(mockk(), mockk())
        assertEquals(
            null,
            useCase("posts", UserContext("userId"), 25, 0)
        )
    }

}

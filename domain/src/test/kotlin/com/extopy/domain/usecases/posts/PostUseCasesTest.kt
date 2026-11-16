package com.extopy.domain.usecases.posts

import com.extopy.domain.exceptions.posts.PostBodyEmptyException
import com.extopy.domain.exceptions.posts.PostReplyAndRepostException
import com.extopy.domain.exceptions.posts.RepliedPostNotFoundException
import com.extopy.domain.exceptions.posts.RepostedPostNotFoundException
import com.extopy.domain.exceptions.validation.ValidationException
import com.extopy.domain.models.posts.Post
import com.extopy.domain.models.posts.PostPayload
import com.extopy.domain.repositories.PostsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.time.Instant
import kotlin.uuid.Uuid

class PostUseCasesTest {

    private val userId = Uuid.parse("00000000-0000-4000-8000-000000000001")
    private val postId = Uuid.parse("00000000-0000-4000-8000-000000000002")
    private val otherPostId = Uuid.parse("00000000-0000-4000-8000-000000000003")

    private val post = Post(
        id = postId,
        userId = userId,
        body = "Hello world!",
        publishedAt = Instant.parse("2026-01-02T03:04:05Z"),
    )

    @Test
    fun testPublishing() = runTest {
        val repository = mockk<PostsRepository>()
        coEvery { repository.create(any(), userId) } returns post

        val result = CreatePostUseCaseImpl(repository)(PostPayload("Hello world!"), userId)

        assertEquals(post, result)
    }

    @Test
    fun testAPostWithNothingInIt() = runTest {
        val repository = mockk<PostsRepository>()

        assertFailsWith<PostBodyEmptyException> {
            CreatePostUseCaseImpl(repository)(PostPayload("   "), userId)
        }
    }

    /**
     * A repost is allowed to say nothing of its own.
     */
    @Test
    fun testARepostMaySayNothing() = runTest {
        val repository = mockk<PostsRepository>()
        coEvery { repository.get(otherPostId, userId) } returns post
        coEvery { repository.create(any(), userId) } returns post

        val result = CreatePostUseCaseImpl(repository)(PostPayload("", repostOfId = otherPostId), userId)

        assertEquals(post, result)
    }

    @Test
    fun testAPostCannotBeBothAReplyAndARepost() = runTest {
        val repository = mockk<PostsRepository>()

        assertFailsWith<PostReplyAndRepostException> {
            CreatePostUseCaseImpl(repository)(
                PostPayload("Hello", repliedToId = otherPostId, repostOfId = postId),
                userId
            )
        }
    }

    /**
     * Replying to or reposting something the account cannot read is not a thing.
     */
    @Test
    fun testReplyingToSomethingThatIsNotThere() = runTest {
        val repository = mockk<PostsRepository>()
        coEvery { repository.get(otherPostId, userId) } returns null

        assertFailsWith<RepliedPostNotFoundException> {
            CreatePostUseCaseImpl(repository)(PostPayload("Hello", repliedToId = otherPostId), userId)
        }
    }

    @Test
    fun testRepostingSomethingThatIsNotThere() = runTest {
        val repository = mockk<PostsRepository>()
        coEvery { repository.get(otherPostId, userId) } returns null

        assertFailsWith<RepostedPostNotFoundException> {
            CreatePostUseCaseImpl(repository)(PostPayload("Hello", repostOfId = otherPostId), userId)
        }
    }

    @Test
    fun testABodyThatIsTooLong() = runTest {
        val repository = mockk<PostsRepository>()

        val exception = assertFailsWith<ValidationException> {
            CreatePostUseCaseImpl(repository)(PostPayload("a".repeat(Post.BODY_MAX_LENGTH + 1)), userId)
        }

        assertEquals("posts_body_maxLength", exception.key)
    }

    /**
     * Reading the post back after an edit has to carry the account doing it, or the query returns
     * nothing and a write that went through reads as a failure.
     */
    @Test
    fun testEditingReadsThePostBackAsTheAuthor() = runTest {
        val repository = mockk<PostsRepository>()
        coEvery { repository.update(postId, any()) } returns true
        coEvery { repository.get(postId, userId) } returns post.copy(body = "Hello again")

        val result = UpdatePostUseCaseImpl(repository)(postId, PostPayload("Hello again"), userId)

        assertEquals("Hello again", result?.body)
        coVerify { repository.get(postId, userId) }
    }

    @Test
    fun testEditingToNothing() = runTest {
        val repository = mockk<PostsRepository>()

        assertFailsWith<PostBodyEmptyException> {
            UpdatePostUseCaseImpl(repository)(postId, PostPayload(""), userId)
        }
    }

    @Test
    fun testAnEditThatTouchedNothing() = runTest {
        val repository = mockk<PostsRepository>()
        coEvery { repository.update(postId, any()) } returns false

        assertNull(UpdatePostUseCaseImpl(repository)(postId, PostPayload("Hello again"), userId))
    }

}

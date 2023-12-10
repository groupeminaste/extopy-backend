package me.nathanfallet.extopy.usecases.posts

import io.ktor.http.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.posts.PostPayload
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.extopy.repositories.posts.IPostsRepository
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CreatePostUseCaseTest {

    @Test
    fun testInvokeWithBody() = runBlocking {
        val repository = mockk<IPostsRepository>()
        val useCase = CreatePostUseCase(repository)
        val post = mockk<Post>()
        val payload = PostPayload("body")
        val context = mockk<UserContext>()
        coEvery { repository.create(payload, context) } returns post
        val result = useCase(payload, context)
        assertEquals(post, result)
    }

    @Test
    fun testInvokeWithEmpty() = runBlocking {
        val repository = mockk<IPostsRepository>()
        val useCase = CreatePostUseCase(repository)
        val post = mockk<Post>()
        val payload = PostPayload("")
        val context = mockk<UserContext>()
        coEvery { repository.create(payload, context) } returns post
        val exception = assertFailsWith<ControllerException> {
            useCase(payload, context)
        }
        assertEquals(HttpStatusCode.BadRequest, exception.code)
        assertEquals("posts_body_empty", exception.key)
    }

    @Test
    fun testInvokeReply() = runBlocking {
        val repository = mockk<IPostsRepository>()
        val useCase = CreatePostUseCase(repository)
        val post = mockk<Post>()
        val payload = PostPayload("body", repliedToId = "replyId")
        val context = mockk<UserContext>()
        coEvery { repository.get("replyId") } returns mockk()
        coEvery { repository.create(payload, context) } returns post
        val result = useCase(payload, context)
        assertEquals(post, result)
    }

    @Test
    fun testInvokeReplyNotExists() = runBlocking {
        val repository = mockk<IPostsRepository>()
        val useCase = CreatePostUseCase(repository)
        val post = mockk<Post>()
        val payload = PostPayload("body", repliedToId = "replyId")
        val context = mockk<UserContext>()
        coEvery { repository.get("replyId") } returns null
        coEvery { repository.create(payload, context) } returns post
        val exception = assertFailsWith<ControllerException> {
            useCase(payload, context)
        }
        assertEquals(HttpStatusCode.BadRequest, exception.code)
        assertEquals("posts_replied_to_not_found", exception.key)
    }

    @Test
    fun testInvokeReplyNoBody() = runBlocking {
        val repository = mockk<IPostsRepository>()
        val useCase = CreatePostUseCase(repository)
        val post = mockk<Post>()
        val payload = PostPayload("", repliedToId = "replyId")
        val context = mockk<UserContext>()
        coEvery { repository.get("replyId") } returns mockk()
        coEvery { repository.create(payload, context) } returns post
        val exception = assertFailsWith<ControllerException> {
            useCase(payload, context)
        }
        assertEquals(HttpStatusCode.BadRequest, exception.code)
        assertEquals("posts_body_empty", exception.key)
    }

    @Test
    fun testInvokeRepost() = runBlocking {
        val repository = mockk<IPostsRepository>()
        val useCase = CreatePostUseCase(repository)
        val post = mockk<Post>()
        val payload = PostPayload("", repostOfId = "repostId")
        val context = mockk<UserContext>()
        coEvery { repository.get("repostId") } returns mockk()
        coEvery { repository.create(payload, context) } returns post
        val result = useCase(payload, context)
        assertEquals(post, result)
    }

    @Test
    fun testInvokeRepostWithBody() = runBlocking {
        val repository = mockk<IPostsRepository>()
        val useCase = CreatePostUseCase(repository)
        val post = mockk<Post>()
        val payload = PostPayload("body", repostOfId = "repostId")
        val context = mockk<UserContext>()
        coEvery { repository.get("repostId") } returns mockk()
        coEvery { repository.create(payload, context) } returns post
        val result = useCase(payload, context)
        assertEquals(post, result)
    }

    @Test
    fun testInvokeRepostNotExists() = runBlocking {
        val repository = mockk<IPostsRepository>()
        val useCase = CreatePostUseCase(repository)
        val post = mockk<Post>()
        val payload = PostPayload("", repostOfId = "repostId")
        val context = mockk<UserContext>()
        coEvery { repository.get("repostId") } returns null
        coEvery { repository.create(payload, context) } returns post
        val exception = assertFailsWith<ControllerException> {
            useCase(payload, context)
        }
        assertEquals(HttpStatusCode.BadRequest, exception.code)
        assertEquals("posts_repost_of_not_found", exception.key)
    }

    @Test
    fun testInvokeReplyRepost() = runBlocking {
        val repository = mockk<IPostsRepository>()
        val useCase = CreatePostUseCase(repository)
        val payload = PostPayload("body", repliedToId = "replyId", repostOfId = "repostId")
        val context = mockk<UserContext>()
        val exception = assertFailsWith<ControllerException> {
            useCase(payload, context)
        }
        assertEquals(HttpStatusCode.BadRequest, exception.code)
        assertEquals("posts_can_only_one_in_reply_or_repost", exception.key)
    }

}

package com.extopy.infrastructure.database.repositories

import com.extopy.domain.models.application.Pagination
import com.extopy.domain.models.posts.PostPayload
import com.extopy.domain.models.users.CreateUserPayload
import com.extopy.infrastructure.database.DatabaseTestHelper
import com.extopy.infrastructure.database.TransactionManager
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PostsDatabaseRepositoryTest {

    private class Fixture(name: String) {
        val transactionManager: TransactionManager = DatabaseTestHelper.transactionManager("posts-$name")
        val users = UsersDatabaseRepository(transactionManager)
        val posts = PostsDatabaseRepository(transactionManager)
        val likes = LikesInPostsDatabaseRepository(transactionManager)
        val followers = FollowersInUsersDatabaseRepository(transactionManager)

        suspend fun user(username: String) = assertNotNull(
            users.create(
                CreateUserPayload(
                    username = username,
                    displayName = username,
                    email = "$username@extopy.com",
                    password = "hashed",
                    birthdate = LocalDate.parse("2002-12-24"),
                )
            )
        )
    }

    @Test
    fun testCreateThenGet() = runTest {
        val fixture = Fixture("create")
        val author = fixture.user("nathanfallet")

        val created = assertNotNull(fixture.posts.create(PostPayload("Hello world!"), author.id))
        val read = fixture.posts.get(created.id, author.id)

        assertEquals("Hello world!", read?.body)
        assertEquals(author.id, read?.userId)
        // The author comes along with the post, which is what the timeline renders from.
        assertEquals("nathanfallet", read?.user?.username)
    }

    @Test
    fun testGetOfAnUnknownPost() = runTest {
        val fixture = Fixture("get-unknown")
        val author = fixture.user("nathanfallet")
        val created = assertNotNull(fixture.posts.create(PostPayload("Hello world!"), author.id))
        fixture.posts.create(PostPayload("Another one"), author.id)

        assertEquals("Hello world!", fixture.posts.get(created.id, author.id)?.body)
    }

    /**
     * The counters and the `likesIn` flag come from the same join every read goes through.
     */
    @Test
    fun testTheCountersAreCountedFromWhatPointsAtThePost() = runTest {
        val fixture = Fixture("counters")
        val author = fixture.user("nathanfallet")
        val reader = fixture.user("someoneelse")
        val post = assertNotNull(fixture.posts.create(PostPayload("Hello world!"), author.id))
        fixture.posts.create(PostPayload("A reply", repliedToId = post.id), reader.id)
        fixture.likes.create(post.id, reader.id)

        val asTheReader = assertNotNull(fixture.posts.get(post.id, reader.id))
        assertEquals(1, asTheReader.likesCount)
        assertEquals(1, asTheReader.repliesCount)
        assertEquals(0, asTheReader.repostsCount)
        assertEquals(true, asTheReader.likesIn)

        // The author has not liked it, so the flag says so for them.
        assertEquals(false, fixture.posts.get(post.id, author.id)?.likesIn)
    }

    @Test
    fun testUpdateStampsThePostAsEdited() = runTest {
        val fixture = Fixture("update")
        val author = fixture.user("nathanfallet")
        val post = assertNotNull(fixture.posts.create(PostPayload("Hello world!"), author.id))

        assertTrue(fixture.posts.update(post.id, PostPayload("Hello again")))

        val read = assertNotNull(fixture.posts.get(post.id, author.id))
        assertEquals("Hello again", read.body)
        assertNotNull(read.editedAt)
    }

    @Test
    fun testDelete() = runTest {
        val fixture = Fixture("delete")
        val author = fixture.user("nathanfallet")
        val post = assertNotNull(fixture.posts.create(PostPayload("Hello world!"), author.id))

        assertTrue(fixture.posts.delete(post.id))
        assertFalse(fixture.posts.delete(post.id))
        assertNull(fixture.posts.get(post.id, author.id))
    }

    @Test
    fun testListReplies() = runTest {
        val fixture = Fixture("replies")
        val author = fixture.user("nathanfallet")
        val post = assertNotNull(fixture.posts.create(PostPayload("Hello world!"), author.id))
        fixture.posts.create(PostPayload("A reply", repliedToId = post.id), author.id)
        fixture.posts.create(PostPayload("Unrelated"), author.id)

        val replies = fixture.posts.listReplies(post.id, Pagination(), author.id)

        assertEquals(1, replies.size)
        assertEquals("A reply", replies.first().body)
    }

    @Test
    fun testListUserPosts() = runTest {
        val fixture = Fixture("user-posts")
        val author = fixture.user("nathanfallet")
        val other = fixture.user("someoneelse")
        fixture.posts.create(PostPayload("Mine"), author.id)
        fixture.posts.create(PostPayload("Theirs"), other.id)

        val mine = fixture.posts.listUserPosts(author.id, Pagination(), author.id)

        assertEquals(1, mine.size)
        assertEquals("Mine", mine.first().body)
    }

    /**
     * The default timeline is what you follow, plus your own.
     */
    @Test
    fun testListDefault() = runTest {
        val fixture = Fixture("default")
        val reader = fixture.user("nathanfallet")
        val followed = fixture.user("followed")
        val stranger = fixture.user("stranger")
        fixture.followers.create(reader.id, followed.id, accepted = true)
        fixture.posts.create(PostPayload("Mine"), reader.id)
        fixture.posts.create(PostPayload("From someone I follow"), followed.id)
        fixture.posts.create(PostPayload("From a stranger"), stranger.id)

        val timeline = fixture.posts.listDefault(Pagination(), reader.id)

        assertEquals(setOf("Mine", "From someone I follow"), timeline.mapNotNull { it.body }.toSet())
    }

    /**
     * A link that has not been accepted yet keeps the posts of that account out of the timeline.
     */
    @Test
    fun testAPendingFollowKeepsThePostsOut() = runTest {
        val fixture = Fixture("pending")
        val reader = fixture.user("nathanfallet")
        val followed = fixture.user("followed")
        fixture.followers.create(reader.id, followed.id, accepted = false)
        fixture.posts.create(PostPayload("Still private"), followed.id)

        assertEquals(emptyList(), fixture.posts.listDefault(Pagination(), reader.id))
    }

    /**
     * The trends order by everything a post got, added up.
     */
    @Test
    fun testListTrends() = runTest {
        val fixture = Fixture("trends")
        val author = fixture.user("nathanfallet")
        val reader = fixture.user("someoneelse")
        val quiet = assertNotNull(fixture.posts.create(PostPayload("Nobody read this"), author.id))
        val loud = assertNotNull(fixture.posts.create(PostPayload("Everybody read this"), author.id))
        fixture.likes.create(loud.id, reader.id)
        fixture.likes.create(loud.id, author.id)

        val trends = fixture.posts.listTrends(Pagination(), reader.id)

        assertEquals(loud.id, trends.first().id)
        assertTrue(trends.any { it.id == quiet.id })
    }

    @Test
    fun testSearchMatchesTheBody() = runTest {
        val fixture = Fixture("search")
        val author = fixture.user("nathanfallet")
        fixture.posts.create(PostPayload("Hello world!"), author.id)
        fixture.posts.create(PostPayload("Something else"), author.id)

        val found = fixture.posts.list(Pagination(search = "world"), author.id)

        assertEquals(1, found.size)
        assertEquals("Hello world!", found.first().body)
    }

}

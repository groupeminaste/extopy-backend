package com.extopy.infrastructure.database.repositories

import com.extopy.domain.models.application.Pagination
import com.extopy.domain.models.users.CreateUserPayload
import com.extopy.infrastructure.database.DatabaseTestHelper
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FollowersInUsersDatabaseRepositoryTest {

    private class Fixture(name: String) {
        val transactionManager = DatabaseTestHelper.transactionManager("followers-$name")
        val users = UsersDatabaseRepository(transactionManager)
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

    /**
     * Listing the followers of an account brings back the accounts on the other end of each link.
     */
    @Test
    fun testListFollowers() = runTest {
        val fixture = Fixture("list")
        val target = fixture.user("target")
        val follower = fixture.user("follower")
        fixture.followers.create(follower.id, target.id, accepted = true)

        val followers = fixture.followers.list(Pagination(), target.id)

        assertEquals(1, followers.size)
        assertEquals(follower.id, followers.first().userId)
        assertEquals("follower", followers.first().user?.username)
    }

    /**
     * Listing what an account follows joins the other end of the link instead.
     */
    @Test
    fun testListFollowing() = runTest {
        val fixture = Fixture("following")
        val target = fixture.user("target")
        val follower = fixture.user("follower")
        fixture.followers.create(follower.id, target.id, accepted = true)

        val following = fixture.followers.listFollowing(Pagination(), follower.id)

        assertEquals(1, following.size)
        assertEquals(target.id, following.first().targetId)
        assertEquals("target", following.first().target?.username)
    }

    /**
     * A link waiting to be accepted is not a link yet, on either side.
     */
    @Test
    fun testAPendingLinkIsNotListed() = runTest {
        val fixture = Fixture("pending")
        val target = fixture.user("target")
        val follower = fixture.user("follower")
        fixture.followers.create(follower.id, target.id, accepted = false)

        assertEquals(emptyList(), fixture.followers.list(Pagination(), target.id))
        assertEquals(emptyList(), fixture.followers.listFollowing(Pagination(), follower.id))
    }

    @Test
    fun testDelete() = runTest {
        val fixture = Fixture("delete")
        val target = fixture.user("target")
        val follower = fixture.user("follower")
        fixture.followers.create(follower.id, target.id, accepted = true)

        assertTrue(fixture.followers.delete(follower.id, target.id))
        assertFalse(fixture.followers.delete(follower.id, target.id))
        assertEquals(emptyList(), fixture.followers.list(Pagination(), target.id))
    }

    /**
     * The two ends of a link are not interchangeable: removing it the wrong way round removes
     * nothing.
     */
    @Test
    fun testDeletingTheOtherWayRoundRemovesNothing() = runTest {
        val fixture = Fixture("delete-reversed")
        val target = fixture.user("target")
        val follower = fixture.user("follower")
        fixture.followers.create(follower.id, target.id, accepted = true)

        assertFalse(fixture.followers.delete(target.id, follower.id))
        assertEquals(1, fixture.followers.list(Pagination(), target.id).size)
    }

}

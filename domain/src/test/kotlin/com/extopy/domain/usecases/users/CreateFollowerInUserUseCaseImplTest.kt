package com.extopy.domain.usecases.users

import com.extopy.domain.exceptions.users.FollowSelfForbiddenException
import com.extopy.domain.exceptions.users.UserNotFoundException
import com.extopy.domain.models.users.FollowerInUser
import com.extopy.domain.models.users.User
import com.extopy.domain.repositories.FollowersInUsersRepository
import com.extopy.domain.repositories.UsersRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid

class CreateFollowerInUserUseCaseImplTest {

    private val userId = Uuid.parse("00000000-0000-4000-8000-000000000001")
    private val targetId = Uuid.parse("00000000-0000-4000-8000-000000000002")

    private val target = User(id = targetId, displayName = "Someone Else", username = "someoneelse")

    private val link = FollowerInUser(userId = userId, targetId = targetId)

    /**
     * A public account is followed right away.
     */
    @Test
    fun testFollowingAPublicAccount() = runTest {
        val repository = mockk<FollowersInUsersRepository>()
        val usersRepository = mockk<UsersRepository>()
        coEvery { usersRepository.get(targetId, userId) } returns target.copy(personal = false)
        coEvery { repository.create(userId, targetId, true) } returns link

        CreateFollowerInUserUseCaseImpl(repository, usersRepository)(targetId, userId)

        coVerify { repository.create(userId, targetId, accepted = true) }
    }

    /**
     * Following a personal account is a request: it only goes live once that account accepts it.
     */
    @Test
    fun testFollowingAPersonalAccountIsARequest() = runTest {
        val repository = mockk<FollowersInUsersRepository>()
        val usersRepository = mockk<UsersRepository>()
        coEvery { usersRepository.get(targetId, userId) } returns target.copy(personal = true)
        coEvery { repository.create(userId, targetId, false) } returns link.copy(accepted = false)

        CreateFollowerInUserUseCaseImpl(repository, usersRepository)(targetId, userId)

        coVerify { repository.create(userId, targetId, accepted = false) }
    }

    @Test
    fun testFollowingYourself() = runTest {
        val repository = mockk<FollowersInUsersRepository>()
        val usersRepository = mockk<UsersRepository>()

        assertFailsWith<FollowSelfForbiddenException> {
            CreateFollowerInUserUseCaseImpl(repository, usersRepository)(userId, userId)
        }
    }

    /**
     * The account was not there: that is a 404, not the 500 the previous version answered with.
     */
    @Test
    fun testFollowingSomethingThatIsNotThere() = runTest {
        val repository = mockk<FollowersInUsersRepository>()
        val usersRepository = mockk<UsersRepository>()
        coEvery { usersRepository.get(targetId, userId) } returns null

        assertFailsWith<UserNotFoundException> {
            CreateFollowerInUserUseCaseImpl(repository, usersRepository)(targetId, userId)
        }
    }

}

package com.extopy.client

import com.extopy.models.application.ExtopyEnvironment
import com.extopy.repositories.auth.AuthAPIRemoteRepository
import com.extopy.repositories.posts.LikesInPostsRemoteRepository
import com.extopy.repositories.posts.PostsRemoteRepository
import com.extopy.repositories.timelines.TimelinesRemoteRepository
import com.extopy.repositories.users.FollowersInUsersRemoteRepository
import com.extopy.repositories.users.UsersRemoteRepository
import dev.kaccelero.client.AbstractAPIClient
import dev.kaccelero.commons.auth.IGetTokenUseCase
import dev.kaccelero.commons.auth.ILogoutUseCase
import dev.kaccelero.commons.auth.IRenewTokenUseCase
import io.ktor.http.*

class ExtopyClient(
    getTokenUseCase: IGetTokenUseCase,
    renewTokenUseCase: IRenewTokenUseCase,
    logoutUseCase: ILogoutUseCase,
    environment: ExtopyEnvironment = ExtopyEnvironment.PRODUCTION,
) : AbstractAPIClient(
    environment.baseUrl,
    getTokenUseCase,
    renewTokenUseCase,
    logoutUseCase
), IExtopyClient {

    override val auth = AuthAPIRemoteRepository(this)
    override val users = UsersRemoteRepository(this)
    override val followersInUsers = FollowersInUsersRemoteRepository(this, users)
    override val posts = PostsRemoteRepository(this)
    override val likesInPosts = LikesInPostsRemoteRepository(this, posts)
    override val timelines = TimelinesRemoteRepository(this)

    override fun shouldIncludeToken(method: HttpMethod, path: String): Boolean {
        // Don't include token for refresh token endpoint (to avoid infinite loop)
        return path != "/api/v1/auth/refresh"
    }

}

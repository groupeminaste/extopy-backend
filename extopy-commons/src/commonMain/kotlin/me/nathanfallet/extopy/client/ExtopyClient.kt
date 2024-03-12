package me.nathanfallet.extopy.client

import me.nathanfallet.extopy.models.application.ExtopyEnvironment
import me.nathanfallet.extopy.models.application.ExtopyJson
import me.nathanfallet.extopy.repositories.auth.AuthAPIRemoteRepository
import me.nathanfallet.extopy.repositories.posts.LikesInPostsRemoteRepository
import me.nathanfallet.extopy.repositories.posts.PostsRemoteRepository
import me.nathanfallet.extopy.repositories.timelines.TimelinesRemoteRepository
import me.nathanfallet.extopy.repositories.users.FollowersInUsersRemoteRepository
import me.nathanfallet.extopy.repositories.users.UsersRemoteRepository
import me.nathanfallet.ktorx.models.api.AbstractAPIClient
import me.nathanfallet.ktorx.usecases.api.IGetTokenUseCase

class ExtopyClient(
    getTokenUseCase: IGetTokenUseCase,
    environment: ExtopyEnvironment = ExtopyEnvironment.PRODUCTION,
) : AbstractAPIClient(
    environment.baseUrl,
    getTokenUseCase,
    ExtopyJson.json
), IExtopyClient {

    override val auth = AuthAPIRemoteRepository(this)
    override val users = UsersRemoteRepository(this)
    override val followersInUsers = FollowersInUsersRemoteRepository(this, users)
    override val posts = PostsRemoteRepository(this)
    override val likesInPosts = LikesInPostsRemoteRepository(this, posts)
    override val timelines = TimelinesRemoteRepository(this)

}

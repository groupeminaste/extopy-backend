package me.nathanfallet.extopy.client

import me.nathanfallet.extopy.repositories.posts.IPostsRemoteRepository
import me.nathanfallet.extopy.repositories.timelines.ITimelinesRemoteRepository
import me.nathanfallet.extopy.repositories.users.IFollowersInUsersRemoteRepository
import me.nathanfallet.extopy.repositories.users.IUsersRemoteRepository
import me.nathanfallet.ktorx.models.api.IAPIClient
import me.nathanfallet.ktorx.repositories.auth.IAuthAPIRemoteRepository

interface IExtopyClient : IAPIClient {

    val auth: IAuthAPIRemoteRepository
    val users: IUsersRemoteRepository
    val followersInUsers: IFollowersInUsersRemoteRepository
    val posts: IPostsRemoteRepository
    val timelines: ITimelinesRemoteRepository

}

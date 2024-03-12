package me.nathanfallet.extopy.client

import me.nathanfallet.extopy.repositories.auth.IAuthAPIRemoteRepository
import me.nathanfallet.extopy.repositories.posts.ILikesInPostsRemoteRepository
import me.nathanfallet.extopy.repositories.posts.IPostsRemoteRepository
import me.nathanfallet.extopy.repositories.timelines.ITimelinesRemoteRepository
import me.nathanfallet.extopy.repositories.users.IFollowersInUsersRemoteRepository
import me.nathanfallet.extopy.repositories.users.IUsersRemoteRepository
import me.nathanfallet.ktorx.models.api.IAPIClient

interface IExtopyClient : IAPIClient {

    val auth: IAuthAPIRemoteRepository
    val users: IUsersRemoteRepository
    val followersInUsers: IFollowersInUsersRemoteRepository
    val posts: IPostsRemoteRepository
    val likesInPosts: ILikesInPostsRemoteRepository
    val timelines: ITimelinesRemoteRepository

}

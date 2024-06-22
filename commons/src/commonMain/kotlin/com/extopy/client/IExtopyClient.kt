package com.extopy.client

import com.extopy.repositories.auth.IAuthAPIRemoteRepository
import com.extopy.repositories.posts.ILikesInPostsRemoteRepository
import com.extopy.repositories.posts.IPostsRemoteRepository
import com.extopy.repositories.timelines.ITimelinesRemoteRepository
import com.extopy.repositories.users.IFollowersInUsersRemoteRepository
import com.extopy.repositories.users.IUsersRemoteRepository
import dev.kaccelero.client.IAPIClient

interface IExtopyClient : IAPIClient {

    val auth: IAuthAPIRemoteRepository
    val users: IUsersRemoteRepository
    val followersInUsers: IFollowersInUsersRemoteRepository
    val posts: IPostsRemoteRepository
    val likesInPosts: ILikesInPostsRemoteRepository
    val timelines: ITimelinesRemoteRepository

}

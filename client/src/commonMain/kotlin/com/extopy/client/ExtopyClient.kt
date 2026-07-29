package com.extopy.client

import com.extopy.client.api.auth.AuthApiClient
import com.extopy.client.api.posts.LikesInPostsApiClient
import com.extopy.client.api.posts.PostsApiClient
import com.extopy.client.api.timelines.TimelinesApiClient
import com.extopy.client.api.users.FollowersInUsersApiClient
import com.extopy.client.api.users.UsersApiClient

/**
 * The Extopy API, as a client talks to it.
 */
interface ExtopyClient {

    val auth: AuthApiClient
    val users: UsersApiClient
    val followersInUsers: FollowersInUsersApiClient
    val posts: PostsApiClient
    val likesInPosts: LikesInPostsApiClient
    val timelines: TimelinesApiClient

}

package me.nathanfallet.extopy.client

import me.nathanfallet.extopy.repositories.auth.IAuthRemoteRepository
import me.nathanfallet.extopy.repositories.posts.IPostsRemoteRepository
import me.nathanfallet.extopy.repositories.users.IUsersRemoteRepository
import me.nathanfallet.ktorx.models.api.IAPIClient

interface IExtopyClient : IAPIClient {

    val auth: IAuthRemoteRepository
    val users: IUsersRemoteRepository
    val posts: IPostsRemoteRepository

}

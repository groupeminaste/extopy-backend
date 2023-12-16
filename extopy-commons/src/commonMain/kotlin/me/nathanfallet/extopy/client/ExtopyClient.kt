package me.nathanfallet.extopy.client

import me.nathanfallet.extopy.models.application.ExtopyEnvironment
import me.nathanfallet.extopy.models.application.ExtopyJson
import me.nathanfallet.extopy.repositories.posts.PostsRemoteRepository
import me.nathanfallet.extopy.repositories.users.UsersRemoteRepository
import me.nathanfallet.ktorx.models.api.AbstractAPIClient
import me.nathanfallet.ktorx.usecases.api.IGetTokenUseCase

class ExtopyClient(
    getTokenUseCase: IGetTokenUseCase? = null,
    environment: ExtopyEnvironment = ExtopyEnvironment.PRODUCTION,
) : AbstractAPIClient(
    environment.baseUrl,
    getTokenUseCase,
    ExtopyJson.json
), IExtopyClient {

    override val users = UsersRemoteRepository(this)
    override val posts = PostsRemoteRepository(this)

}

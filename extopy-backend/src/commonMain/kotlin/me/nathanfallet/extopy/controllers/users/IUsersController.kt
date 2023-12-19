package me.nathanfallet.extopy.controllers.users

import io.ktor.server.application.*
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.users.CreateUserPayload
import me.nathanfallet.extopy.models.users.UpdateUserPayload
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.ktorx.controllers.IModelController

interface IUsersController : IModelController<User, String, CreateUserPayload, UpdateUserPayload> {

    suspend fun getPosts(call: ApplicationCall, id: String): List<Post>

}

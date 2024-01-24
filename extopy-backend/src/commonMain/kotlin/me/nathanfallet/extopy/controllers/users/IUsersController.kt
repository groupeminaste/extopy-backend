package me.nathanfallet.extopy.controllers.users

import io.ktor.server.application.*
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.users.CreateUserPayload
import me.nathanfallet.extopy.models.users.UpdateUserPayload
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.ktorx.controllers.IModelController
import me.nathanfallet.ktorx.models.annotations.*

interface IUsersController : IModelController<User, String, CreateUserPayload, UpdateUserPayload> {

    @APIMapping
    @GetPath
    suspend fun get(call: ApplicationCall, @Id id: String): User

    @APIMapping
    @UpdatePath
    suspend fun update(call: ApplicationCall, @Id id: String, @Payload payload: UpdateUserPayload): User

    @APIMapping("listUserPost", "Get user posts by id")
    @Path("GET", "/{userId}/posts")
    suspend fun listPosts(call: ApplicationCall, @Id id: String): List<Post>

}

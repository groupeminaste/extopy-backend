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
    @GetModelPath
    @DocumentedError(401, "auth_invalid_credentials")
    @DocumentedError(404, "users_not_found")
    suspend fun get(call: ApplicationCall, @Id id: String): User

    @APIMapping
    @UpdateModelPath
    @DocumentedError(401, "auth_invalid_credentials")
    @DocumentedError(403, "users_update_not_allowed")
    @DocumentedError(404, "users_not_found")
    suspend fun update(call: ApplicationCall, @Id id: String, @Payload payload: UpdateUserPayload): User

    @APIMapping("listUserPost", "Get user posts by id")
    @Path("GET", "/{userId}/posts")
    @DocumentedError(401, "auth_invalid_credentials")
    @DocumentedError(404, "users_not_found")
    suspend fun listPosts(call: ApplicationCall, @Id id: String): List<Post>

}

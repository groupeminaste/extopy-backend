package com.extopy.controllers.users

import com.extopy.models.posts.Post
import com.extopy.models.users.CreateUserPayload
import com.extopy.models.users.UpdateUserPayload
import com.extopy.models.users.User
import dev.kaccelero.annotations.*
import dev.kaccelero.controllers.IModelController
import dev.kaccelero.models.UUID
import io.ktor.server.application.*

interface IUsersController : IModelController<User, UUID, CreateUserPayload, UpdateUserPayload> {

    @APIMapping
    @ListModelPath
    @DocumentedError(401, "auth_invalid_credentials")
    suspend fun list(
        call: ApplicationCall,
        @QueryParameter limit: Long?,
        @QueryParameter offset: Long?,
        @QueryParameter search: String?,
    ): List<User>

    @APIMapping
    @GetModelPath
    @DocumentedError(401, "auth_invalid_credentials")
    @DocumentedError(404, "users_not_found")
    suspend fun get(call: ApplicationCall, @Id id: UUID): User

    @APIMapping
    @UpdateModelPath
    @DocumentedError(401, "auth_invalid_credentials")
    @DocumentedError(403, "users_update_not_allowed")
    @DocumentedError(404, "users_not_found")
    suspend fun update(call: ApplicationCall, @Id id: UUID, @Payload payload: UpdateUserPayload): User

    @APIMapping("listUserPost", "Get user posts by id")
    @Path("GET", "/{userId}/posts")
    @DocumentedError(401, "auth_invalid_credentials")
    @DocumentedError(404, "users_not_found")
    suspend fun listPosts(
        call: ApplicationCall,
        @Id id: UUID,
        @QueryParameter limit: Long?,
        @QueryParameter offset: Long?,
    ): List<Post>

}

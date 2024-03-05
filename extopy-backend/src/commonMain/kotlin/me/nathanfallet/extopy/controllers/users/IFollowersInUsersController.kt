package me.nathanfallet.extopy.controllers.users

import io.ktor.server.application.*
import me.nathanfallet.extopy.models.users.FollowerInUser
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.ktorx.controllers.IChildModelController
import me.nathanfallet.ktorx.models.annotations.*

interface IFollowersInUsersController : IChildModelController<FollowerInUser, String, Unit, Unit, User, String> {

    @APIMapping
    @ListModelPath
    @DocumentedError(401, "auth_invalid_credentials")
    @DocumentedError(404, "users_not_found")
    suspend fun list(
        call: ApplicationCall,
        @ParentModel parent: User,
        @QueryParameter limit: Long?,
        @QueryParameter offset: Long?,
    ): List<FollowerInUser>

    @APIMapping
    @Path("GET", "/following")
    @DocumentedError(401, "auth_invalid_credentials")
    @DocumentedError(404, "users_not_found")
    suspend fun listFollowing(
        call: ApplicationCall,
        @ParentModel parent: User,
        @QueryParameter limit: Long?,
        @QueryParameter offset: Long?,
    ): List<FollowerInUser>

    @APIMapping
    @CreateModelPath
    @DocumentedError(401, "auth_invalid_credentials")
    @DocumentedError(404, "users_not_found")
    @DocumentedError(500, "error_internal")
    suspend fun create(
        call: ApplicationCall,
        @ParentModel parent: User,
        @Payload payload: Unit,
    ): FollowerInUser

    @APIMapping
    @DeleteModelPath
    @DocumentedType(FollowerInUser::class)
    @DocumentedError(401, "auth_invalid_credentials")
    @DocumentedError(403, "followers_in_users_delete_not_allowed")
    @DocumentedError(404, "users_not_found")
    @DocumentedError(500, "error_internal")
    suspend fun delete(call: ApplicationCall, @ParentModel parent: User, @Id id: String)

}

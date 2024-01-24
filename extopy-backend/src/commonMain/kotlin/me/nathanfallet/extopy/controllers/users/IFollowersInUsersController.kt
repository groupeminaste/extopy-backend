package me.nathanfallet.extopy.controllers.users

import io.ktor.server.application.*
import me.nathanfallet.extopy.models.users.FollowerInUser
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.ktorx.controllers.IChildModelController
import me.nathanfallet.ktorx.models.annotations.*

interface IFollowersInUsersController : IChildModelController<FollowerInUser, String, Unit, Unit, User, String> {

    @APIMapping
    @ListPath
    suspend fun list(call: ApplicationCall, @ParentModel("userId") parent: User): List<FollowerInUser>

    @APIMapping
    @Path("GET", "/following")
    suspend fun listFollowing(call: ApplicationCall, @ParentModel("userId") parent: User): List<FollowerInUser>

    @APIMapping
    @CreatePath
    suspend fun create(
        call: ApplicationCall,
        @ParentModel("userId") parent: User,
        @Payload payload: Unit,
    ): FollowerInUser

    @APIMapping
    @DeletePath
    suspend fun delete(call: ApplicationCall, @ParentModel("userId") parent: User, @Id id: String)

}

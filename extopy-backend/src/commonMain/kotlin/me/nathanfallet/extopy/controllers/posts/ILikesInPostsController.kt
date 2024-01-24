package me.nathanfallet.extopy.controllers.posts

import io.ktor.server.application.*
import me.nathanfallet.extopy.models.posts.LikeInPost
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.ktorx.controllers.IChildModelController
import me.nathanfallet.ktorx.models.annotations.*

interface ILikesInPostsController : IChildModelController<LikeInPost, String, Unit, Unit, Post, String> {

    @APIMapping
    @ListPath
    suspend fun list(call: ApplicationCall, @ParentModel("postId") parent: Post): List<LikeInPost>

    @APIMapping
    @CreatePath
    suspend fun create(call: ApplicationCall, @ParentModel("postId") parent: Post, @Payload payload: Unit): LikeInPost

    @APIMapping
    @DeletePath
    suspend fun delete(call: ApplicationCall, @ParentModel("postId") parent: Post, @Id id: String)

}

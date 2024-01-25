package me.nathanfallet.extopy.controllers.posts

import io.ktor.server.application.*
import me.nathanfallet.extopy.models.posts.LikeInPost
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.ktorx.controllers.IChildModelController
import me.nathanfallet.ktorx.models.annotations.*

interface ILikesInPostsController : IChildModelController<LikeInPost, String, Unit, Unit, Post, String> {

    @APIMapping
    @ListModelPath
    @DocumentedError(401, "auth_invalid_credentials")
    @DocumentedError(404, "posts_not_found")
    suspend fun list(call: ApplicationCall, @ParentModel("postId") parent: Post): List<LikeInPost>

    @APIMapping
    @CreateModelPath
    @DocumentedError(401, "auth_invalid_credentials")
    @DocumentedError(404, "posts_not_found")
    @DocumentedError(500, "error_internal")
    suspend fun create(call: ApplicationCall, @ParentModel("postId") parent: Post, @Payload payload: Unit): LikeInPost

    @APIMapping
    @DeleteModelPath
    @DocumentedType(LikeInPost::class)
    @DocumentedError(401, "auth_invalid_credentials")
    @DocumentedError(403, "likes_in_posts_delete_not_allowed")
    @DocumentedError(404, "posts_not_found")
    @DocumentedError(500, "error_internal")
    suspend fun delete(call: ApplicationCall, @ParentModel("postId") parent: Post, @Id id: String)

}

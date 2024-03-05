package me.nathanfallet.extopy.controllers.posts

import io.ktor.server.application.*
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.posts.PostPayload
import me.nathanfallet.ktorx.controllers.IModelController
import me.nathanfallet.ktorx.models.annotations.*

interface IPostsController : IModelController<Post, String, PostPayload, PostPayload> {

    @APIMapping
    @ListModelPath
    @DocumentedError(401, "auth_invalid_credentials")
    suspend fun list(
        call: ApplicationCall,
        @QueryParameter limit: Long?,
        @QueryParameter offset: Long?,
        @QueryParameter search: String?,
    ): List<Post>

    @APIMapping
    @GetModelPath
    @DocumentedError(401, "auth_invalid_credentials")
    @DocumentedError(404, "posts_not_found")
    suspend fun get(call: ApplicationCall, @Id id: String): Post

    @APIMapping
    @CreateModelPath
    @DocumentedError(401, "auth_invalid_credentials")
    @DocumentedError(500, "error_internal")
    suspend fun create(call: ApplicationCall, @Payload payload: PostPayload): Post

    @APIMapping
    @UpdateModelPath
    @DocumentedError(401, "auth_invalid_credentials")
    @DocumentedError(403, "posts_update_not_allowed")
    @DocumentedError(404, "posts_not_found")
    @DocumentedError(500, "error_internal")
    suspend fun update(call: ApplicationCall, @Id id: String, @Payload payload: PostPayload): Post

    @APIMapping
    @DeleteModelPath
    @DocumentedType(Post::class)
    @DocumentedError(401, "auth_invalid_credentials")
    @DocumentedError(403, "posts_delete_not_allowed")
    @DocumentedError(404, "posts_not_found")
    @DocumentedError(500, "error_internal")
    suspend fun delete(call: ApplicationCall, @Id id: String)

    @APIMapping("listPostReply", "Get post replies by id")
    @Path("GET", "/{postId}/replies")
    @DocumentedError(401, "auth_invalid_credentials")
    @DocumentedError(404, "posts_not_found")
    suspend fun listReplies(
        call: ApplicationCall,
        @Id id: String,
        @QueryParameter limit: Long?,
        @QueryParameter offset: Long?,
    ): List<Post>

}

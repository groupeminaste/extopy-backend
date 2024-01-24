package me.nathanfallet.extopy.controllers.posts

import io.ktor.server.application.*
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.posts.PostPayload
import me.nathanfallet.ktorx.controllers.IModelController
import me.nathanfallet.ktorx.models.annotations.*

interface IPostsController : IModelController<Post, String, PostPayload, PostPayload> {

    @APIMapping
    @GetPath
    suspend fun get(call: ApplicationCall, @Id id: String): Post

    @APIMapping
    @CreatePath
    suspend fun create(call: ApplicationCall, @Payload payload: PostPayload): Post

    @APIMapping
    @UpdatePath
    suspend fun update(call: ApplicationCall, @Id id: String, @Payload payload: PostPayload): Post

    @APIMapping
    @DeletePath
    suspend fun delete(call: ApplicationCall, @Id id: String)

    @APIMapping("listPostReply", "Get post replies by id")
    @Path("GET", "/{postId}/replies")
    suspend fun listReplies(call: ApplicationCall, @Id id: String): List<Post>

}

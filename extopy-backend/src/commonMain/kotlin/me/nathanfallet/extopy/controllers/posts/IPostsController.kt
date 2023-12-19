package me.nathanfallet.extopy.controllers.posts

import io.ktor.server.application.*
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.posts.PostPayload
import me.nathanfallet.ktorx.controllers.IModelController

interface IPostsController : IModelController<Post, String, PostPayload, PostPayload> {

    suspend fun getReplies(call: ApplicationCall, id: String): List<Post>

}

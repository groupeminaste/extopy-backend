package me.nathanfallet.extopy.controllers.posts

import io.ktor.util.reflect.*
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.posts.PostPayload
import me.nathanfallet.ktorx.routers.api.APIModelRouter

class PostsRouter(
    controller: IPostsController,
) : APIModelRouter<Post, String, PostPayload, PostPayload>(
    typeInfo<Post>(),
    typeInfo<PostPayload>(),
    typeInfo<PostPayload>(),
    controller,
    IPostsController::class,
    prefix = "/api/v1"
)

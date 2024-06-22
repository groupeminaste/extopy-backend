package com.extopy.controllers.posts

import com.extopy.models.posts.Post
import com.extopy.models.posts.PostPayload
import dev.kaccelero.routers.APIModelRouter
import io.ktor.util.reflect.*

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

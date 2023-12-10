package me.nathanfallet.extopy.controllers.posts

import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.posts.PostPayload
import me.nathanfallet.ktorx.controllers.IModelController
import me.nathanfallet.ktorx.models.api.APIMapping
import me.nathanfallet.ktorx.routers.api.APIModelRouter

class PostsRouter(
    postsController: IModelController<Post, String, PostPayload, PostPayload>,
) : APIModelRouter<Post, String, PostPayload, PostPayload>(
    Post::class,
    PostPayload::class,
    PostPayload::class,
    postsController,
    mapping = APIMapping(
        listEnabled = false
    ),
    prefix = "/api/v1"
)

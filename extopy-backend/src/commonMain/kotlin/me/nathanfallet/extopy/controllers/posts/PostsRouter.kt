package me.nathanfallet.extopy.controllers.posts

import io.ktor.util.reflect.*
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.posts.PostPayload
import me.nathanfallet.ktorx.controllers.IModelController
import me.nathanfallet.ktorx.models.api.APIMapping
import me.nathanfallet.ktorx.routers.api.APIModelRouter

class PostsRouter(
    postsController: IModelController<Post, String, PostPayload, PostPayload>,
) : APIModelRouter<Post, String, PostPayload, PostPayload>(
    typeInfo<Post>(),
    typeInfo<PostPayload>(),
    typeInfo<PostPayload>(),
    typeInfo<List<Post>>(),
    postsController,
    mapping = APIMapping(
        listEnabled = false
    ),
    prefix = "/api/v1"
)

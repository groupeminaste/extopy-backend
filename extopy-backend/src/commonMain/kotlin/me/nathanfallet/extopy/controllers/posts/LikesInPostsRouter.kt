package me.nathanfallet.extopy.controllers.posts

import io.ktor.util.reflect.*
import me.nathanfallet.extopy.models.posts.LikeInPost
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.ktorx.controllers.IChildModelController
import me.nathanfallet.ktorx.models.api.APIMapping
import me.nathanfallet.ktorx.routers.api.APIChildModelRouter

class LikesInPostsRouter(
    controller: IChildModelController<LikeInPost, String, Unit, Unit, Post, String>,
    postsRouter: PostsRouter,
) : APIChildModelRouter<LikeInPost, String, Unit, Unit, Post, String>(
    typeInfo<LikeInPost>(),
    typeInfo<Unit>(),
    typeInfo<Unit>(),
    typeInfo<List<LikeInPost>>(),
    controller,
    postsRouter,
    mapping = APIMapping(
        getEnabled = false,
        updateEnabled = false
    ),
    route = "likes",
    id = "userId",
    prefix = "/api/v1"
)

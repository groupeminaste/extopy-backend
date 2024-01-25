package me.nathanfallet.extopy.controllers.posts

import io.ktor.util.reflect.*
import me.nathanfallet.extopy.models.posts.LikeInPost
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.ktorx.routers.api.APIChildModelRouter

class LikesInPostsRouter(
    controller: ILikesInPostsController,
    postsRouter: PostsRouter,
) : APIChildModelRouter<LikeInPost, String, Unit, Unit, Post, String>(
    typeInfo<LikeInPost>(),
    typeInfo<Unit>(),
    typeInfo<Unit>(),
    controller,
    ILikesInPostsController::class,
    postsRouter,
    route = "likes",
    id = "userId",
    prefix = "/api/v1"
)

package com.extopy.controllers.posts

import com.extopy.models.posts.LikeInPost
import com.extopy.models.posts.Post
import dev.kaccelero.routers.APIChildModelRouter
import io.ktor.util.reflect.*

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
